import './App.css'
import {useEffect, useRef, useState} from "react";
import Cell from "./Cell.tsx";
import {GameState} from "./classes/GameState.ts";
import {toast, ToastContainer} from "react-toastify";
import {Position} from "./classes/dtos/Position.ts";
import {MoveRequest, SolverRequest, UndoRequest, WorkerRequest} from "./classes/dtos/WorkerRequests.ts";
import {initWorker, worker} from "./classes/Worker.ts";
import {
    ErrorResponse,
    MoveResponse,
    ResponseData,
    SolverResponse,
    WorkerResponse
} from "./classes/dtos/WorkerResponses.ts";

const loadingToastId = toast.loading("Loading resources...");
initWorker();

let isLoading: boolean = true;
function App() {
    const [isLoadingState, setIsLoadingState] = useState(isLoading);
    const [maxTime, setMaxTime] = useState(3000);

    const [board, setBoard] = useState(createBoard(6, 7));
    const [gameOver, setGameOver] = useState(false);
    const [currentPlayer, setCurrentPlayer] = useState(1);
    const [gameState, setGameState] = useState(GameState.RUNNING);
    const [moves, setMoves] = useState(new Array<Position>());
    const [score, setScore] = useState(0);
    const [winDistance, setWinDistance] = useState(-1);

    const prevBoardRef = useRef(0);

    worker.onmessage = (event) => {
        console.log("main received", event.data);

        const eventData: WorkerResponse = event.data;
        let payload: ResponseData = eventData.data;
        switch(eventData.type) {
            case 'LOAD':
                toast.update(loadingToastId, {render: "Resources loaded!", type: "success", isLoading: false, autoClose: 5000});
                break;
            case 'UNDO':
                const undoResponse = payload as MoveResponse;
                if(undoResponse.position == null) break;

                setBoard(undoResponse.board);
                setMoves(prev => prev.slice(0, -1));
                setGameState(undoResponse.gameState);
                togglePlayer();
                break;
            case 'MOVE':
                const moveResponse = payload as MoveResponse;
                if(moveResponse.position == null) break;

                setBoard(moveResponse.board);
                setMoves(prev => [...prev, moveResponse.position]);
                setGameState(moveResponse.gameState);
                togglePlayer();
                break;
            case 'BESTMOVE':
                const solverResponse = payload as SolverResponse;

                console.log(solverResponse)
                setBoard(solverResponse.board);
                setMoves(prev => [...prev, solverResponse.bestMove.position]);
                setGameState(solverResponse.gameState);
                setScore(solverResponse.bestMove.score);
                setWinDistance(solverResponse.bestMove.winDistance);
                togglePlayer();
                break;
            case 'ERROR':
                const errorPayload = payload as ErrorResponse;
                toast.warn(errorPayload.message);
                break;
        }

        isLoading = false;
        setIsLoadingState(false);
    }

    //print game state info
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

    //trigger ai move
    useEffect(() => {
        if(!board && gameState == GameState.RUNNING) return;

        //dont start ai move on undo
        const currentPieceCount = countPieces(board);
        const previousPieceCount = prevBoardRef.current;
        prevBoardRef.current = currentPieceCount;
        if(currentPieceCount < previousPieceCount) return;

        //only start ai move when player is -1
        if(currentPlayer && currentPlayer == -1) {
            startBestMove(currentPlayer);
        }
    }, [board, currentPlayer]);

    function togglePlayer() {
        setCurrentPlayer(prev => prev === 1 ? -1 : 1);
    }

    function abortWorker() {
        worker.terminate();

        isLoading = false;
        setIsLoadingState(false);

        if(currentPlayer == -1) {
            //startUndoMove();
        }
    }

    function startBestMove(player: number) {
        if(gameOver || isLoading) return;
        isLoading = true;
        setIsLoadingState(true);

        const solverRequest: SolverRequest = {
            board: board,
            player: player,
            maxTime: maxTime,
            maxDepth: 0
        };
        const workerRequest: WorkerRequest = {
            type: 'BESTMOVE',
            data: solverRequest
        };
        worker.postMessage(workerRequest);
    }

    function startMakeMove(position: Position, player: number) {
        if(gameOver || isLoading) return;
        isLoading = true;
        setIsLoadingState(true);

        const moveRequest: MoveRequest = {
            board: board,
            player: player,
            position: position
        };
        const workerRequest: WorkerRequest = {
            type: 'MOVE',
            data: moveRequest
        };
        worker.postMessage(workerRequest);
    }

    function startUndoMove() {
        if(moves.length === 0 || isLoading) return;
        isLoading = true;
        setIsLoadingState(true);

        const undoRequest: UndoRequest = {
            board: board,
            position: moves[moves.length - 1]
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
                                  lastMove={moves.length == 0 ? null : moves[moves.length - 1]}
                                  makeMove={startMakeMove}
                            />
                        ))}
                    </div>
                ))}

                {isLoadingState && !gameOver && <div id="board-loading">
                    <img src="/loading.gif" alt="Loading" width="30" height="30"/>
                </div>}
            </div>

            <button onClick={abortWorker} disabled={!isLoadingState}>Abort</button>
            <button onClick={startUndoMove} disabled={moves.length === 0 || isLoadingState}>Undo</button>
            <input type="number" placeholder="Time in ms" defaultValue={maxTime} onChange={(event) => {
                setMaxTime(event.target.valueAsNumber);
            }}/>
            <p>score: {score}</p>
            {winDistance >= 0 && <p>Player {score > 0 ? "Yellow" : "Red"} wins in {winDistance} moves!</p>}

            <ToastContainer/>
        </div>
    )
}

export default App

function createBoard(rows: number, cols: number): number[][] {
    /*return [
    //0, 1, 2, 3, 4, 5, 6
     [0, 0, 0, 0, 0, 0, 0],   //0
     [0, 0, 0, 0, 0, 0, 0],   //1
     [0, 0, 0, 0, 0, 0, 0],   //2
     [0, 0, 0, 0, 0, 0, 0],   //3
     [0, 0, 1, 0, 0, 0, 0],   //4
     [0, 0, -1, -1, 0, 0, 0],   //5
    //0, 1, 2, 3, 4, 5, 6
    ];*/

    return new Array(rows)
        .fill(0)
        .map(
            () => new Array(cols).fill(0)
        )
}

function countPieces(board: number[][]) {
    return board.flat().filter(piece => piece !== 0).length;
}