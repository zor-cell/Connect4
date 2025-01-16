import './App.css'
import {computeBestMove, makeMove} from './wasm/initWasm.ts'
import {useState} from "react";
import Cell from "./Cell.tsx";

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
    const [moves, setMoves] = useState(new Array<Position>());

    function togglePlayer() {
        setCurrentPlayer(prev => prev === 1 ? 2 : 1);
    }

    function startBestMove(player: number) {
        computeBestMove(board, player)
            .then(payload => {
                setBoard(payload.board);
            }).catch(err => {
                console.log(err);
            });
    }

    function startMakeMove(position: Position, player: number) {
        makeMove(board, player, position)
            .then(payload => {
                let move: Position = {i: payload.moveI, j: payload.moveJ};

                console.log(payload, move.i, move.j)

                if(move.i >= 0 && move.j >= 0) {
                    setBoard(payload.board);
                    setMoves(prev => [...prev, move]);
                    togglePlayer();
                }
            })
            .catch(err => {
                console.log(err);
            });
    }

    function undoMove() {
        if(moves.length === 0) return;

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
            <button onClick={undoMove} disabled={moves.length === 0}>Undo</button>
            <button>Human vs Human</button>
        </div>
    )
}

export default App
