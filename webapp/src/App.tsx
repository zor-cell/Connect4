import './App.css'
import {Connector} from './wasm/connector.ts'
import {useEffect, useState} from "react";
import Cell from "./Cell.tsx";
import {GameState} from "./classes/GameState.ts";
import {toast, ToastContainer} from "react-toastify";
import {Position} from "./classes/Position.ts";

function createBoard(rows: number, cols: number): number[][] {
    return new Array(rows)
        .fill(0)
        .map(
            () => new Array(cols).fill(0)
        )
}

function App() {
    const [board, setBoard] = useState(createBoard(6, 7));
    const [currentPlayer, setCurrentPlayer] = useState(1);
    const [gameState, setGameState] = useState(GameState.RUNNING);
    const [gameOver, setGameOver] = useState(false);

    const [moves, setMoves] = useState(new Array<Position>());

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

    function togglePlayer() {
        setCurrentPlayer(prev => prev === 1 ? 2 : 1);
    }

    function startBestMove(player: number) {
        if(gameOver) return;

        Connector.computeBestMove(board, player)
            .then(payload => {
                setBoard(payload.board);
            }).catch(err => {
                toast.warn(err)
            });
    }

    function startMakeMove(position: Position, player: number) {
        if(gameOver) return;

        Connector.makeMove(board, player, position)
            .then(payload => {
                if(payload.position == null) return;

                setBoard(payload.board);
                setMoves(prev => [...prev, payload.position]);
                setGameState(payload.gameState);
                togglePlayer();
            })
            .catch(err => {
                toast.warn(err);
            });
    }

    function startUndoMove() {
        if(moves.length === 0) return;

        const lastMove = moves[moves.length - 1];
        Connector.undoMove(board, lastMove)
            .then(payload => {
                if(payload.position == null) return;

                setBoard(payload.board);
                setMoves(prev => prev.slice(0, -1));
                setGameState(payload.gameState);
                togglePlayer();
            })
            .catch(err => {
                toast.warn(err);
            });
        return;
    }

    return (
        <div>
            <div id="board">
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
