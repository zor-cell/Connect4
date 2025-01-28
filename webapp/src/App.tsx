import './App.css'
import {useEffect, useState} from "react";
import Cell from "./Cell.tsx";
import {GameState} from "./classes/GameState.ts";
import {toast, ToastContainer} from "react-toastify";
import {Position} from "./wasm/dtos/Position.ts";
import {MoveRequest, SolverRequest, UndoRequest, WorkerRequest} from "./wasm/dtos/WorkerRequests.ts";
import {worker} from "./wasm/workerConnector.ts";

function createBoard(rows: number, cols: number): number[][] {
    return new Array(rows)
        .fill(0)
        .map(
            () => new Array(cols).fill(0)
        )
}

function App() {
    const [board, setBoard] = useState(createBoard(6, 7));
    const [gameOver, setGameOver] = useState(false);
    const [currentPlayer, setCurrentPlayer] = useState(1);
    const [gameState, setGameState] = useState(GameState.RUNNING);

    const [moves, setMoves] = useState(new Array<Position>());

    worker.onmessage = (event) => {
        console.log("message from worker", event.data);
        const payload = event.data.data;
        switch(event.data.type) {
            case 'LOAD':
                const id = toast.loading("Loading resources...");
                toast.update(id, {render: "Resources loaded!", type: "success", isLoading: false, autoClose: 5000});
                break;
            case 'UNDO':
                if(payload.position == null) return;

                setBoard(payload.board);
                setMoves(prev => prev.slice(0, -1));
                setGameState(payload.gameState);
                togglePlayer();
                break;
            case 'MOVE':
                if(payload.position == null) return;

                setBoard(payload.board);
                setMoves(prev => [...prev, payload.position]);
                setGameState(payload.gameState);
                togglePlayer();
                break;
            case 'BESTMOVE':
                break;
        }
    }
    worker.onerror = (event) => {
        console.log(event.message)
        toast.warn(event.message);
    }

    useEffect(() => {
        //winning info
        if(gameState === GameState.DRAW) {
            toast.info("The game is a draw!");
            setGameOver(true);
        } else if(gameState === GameState.PLAYER1 || gameState === GameState.PLAYER2) {
            let winner = gameState === GameState.PLAYER1 ? "1" : "2";
            toast.info(`Player ${winner} won!`);
            setGameOver(true);
        } else {
            setGameOver(false);
        }
    }, [gameState])

    useEffect(() => {
        if(board && currentPlayer && currentPlayer == -1) {
            startBestMove(currentPlayer);
        }
    }, [board, currentPlayer]);

    function togglePlayer() {
        setCurrentPlayer(prev => prev === 1 ? -1 : 1);
    }

    function startBestMove(player: number) {
        if(gameOver) return;

        const solverRequest: SolverRequest = {
            board: board,
            player: player,
            maxTime: 3000,
            maxDepth: 0
        };
        const workerRequest: WorkerRequest = {
            type: 'BESTMOVE',
            data: solverRequest
        };
        worker.postMessage(workerRequest);
    }

    function startMakeMove(position: Position, player: number) {
        if(gameOver) return;

        const moveRequest: MoveRequest = {
            board: board,
            player: player,
            position: position
        };
        const workerRequest: WorkerRequest = {
            type: 'MOVE',
            data: moveRequest
        };
        console.log("sending ", workerRequest)
        worker.postMessage(workerRequest);
    }

    function startUndoMove() {
        if(moves.length === 0) return;

        const lastMove = moves[moves.length - 1];

        const undoRequest: UndoRequest = {
            board: board,
            position: lastMove
        };
        const workerRequest: WorkerRequest = {
            type: 'UNDO',
            data: undoRequest
        };
        worker.postMessage(workerRequest);
    }

    return (
        <div>
            <div id="board" style={{pointerEvents: gameOver ? "none" : "auto", opacity: gameOver ? 0.8 : 1}}>
                {board.map((row, rowIndex) => (
                    <div key={rowIndex} className="board-row">
                        {row.map((cell, colIndex) => (
                            <Cell key={colIndex}
                                  cellValue={cell}
                                  cellPosition={{i: rowIndex, j: colIndex}}
                                  currentPlayer={currentPlayer}
                                  makeMove={startMakeMove}
                            />
                        ))}
                    </div>
                ))}
            </div>

            <button onClick={() => startBestMove(currentPlayer)}>Best Move</button>
            <button onClick={startUndoMove} disabled={moves.length === 0}>Undo</button>
            <button>Human vs Human</button>

            <ToastContainer/>
        </div>
    )
}

export default App
