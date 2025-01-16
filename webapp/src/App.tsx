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

  function startComputation() {
      computeBestMove(board)
          .then(payload => {
              setBoard(payload.board);
          }).catch(err => {
              console.log(err);
          });
  }

  function startMakeMove(position: Position) {
      makeMove(board, position)
          .then(payload => {
              setBoard(payload.board);
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
