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

    function togglePlayer() {
        setCurrentPlayer((prev) => prev === 1 ? 2 : 1);
    }

  function startComputation() {
      computeBestMove(board)
          .then(payload => {
              setBoard(payload.board);
          }).catch(err => {
              console.log(err);
          });
  }

  function startMakeMove(position: Position, player: number) {
      makeMove(board, player, position)
          .then(payload => {
              setBoard(payload.board);
              togglePlayer();
          })
          .catch(err => {
              console.log(err);
          });
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

          <button onClick={startComputation}>Best Move</button>
      </div>
  )
}

export default App
