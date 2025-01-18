import './App.css'
import {computeBestMove, makeMove} from './wasm/connector.ts'
import {useEffect, useState} from "react";
import Cell from "./Cell.tsx";
import {GameState} from "./classes/GameState.ts";
import {toast, ToastContainer} from "react-toastify";

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

    const [moves, setMoves] = useState(new Array<Position>());

    useEffect(() => {
        //winning info
        if(gameState === GameState.DRAW) {
            toast.info("The game is a draw!")
        } else if(gameState === GameState.PLAYER1 || gameState === GameState.PLAYER2) {
            let winner = gameState === GameState.PLAYER1 ? "1" : "2";
            toast.info(`Player ${winner} won!`);
        }
    }, [gameState])

    function togglePlayer() {
        setCurrentPlayer(prev => prev === 1 ? 2 : 1);
    }

    function startBestMove(player: number) {
        computeBestMove(board, player)
            .then(payload => {
                setBoard(payload.board);
            }).catch(err => {
                toast.warn(err)
            });
    }

    function startMakeMove(position: Position, player: number) {
        makeMove(board, player, position)
            .then(payload => {
                if(payload.position.i >= 0 && payload.position.j >= 0) {
                    setBoard(payload.board);
                    setMoves(prev => [...prev, payload.position]);
                    setGameState(payload.gameState);
                    togglePlayer();
                }
            })
            .catch(err => {
                toast.warn(err);
            });
    }

    function startUndoMove() {
        if(moves.length === 0) return;

        //TODO undo should also run through backend

        const lastMove = moves[moves.length - 1];

        setMoves(prev => prev.slice(0, -1));
        setBoard(prev => {
            prev[lastMove.i][lastMove.j] = 0;
            return prev;
        });
        togglePlayer();
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
