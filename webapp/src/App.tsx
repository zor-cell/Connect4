import './App.css'
import {computeBestMove} from './wasm/initWasm.ts'
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
      let computation: Promise<Payload> = computeBestMove();
      computation.then((payload: Payload) => {
          setBoard(payload.board);
      }).catch((e: any) => {
          console.log(e);
      });
  }

  return (
      <div>
          <div id="board">
              {board.map((row, rowIndex) => (
                  <div key={rowIndex} className="board-row">
                      {row.map((cell, colIndex) => (
                          <Cell key={colIndex} cellValue={cell} cellPosition={{i: rowIndex, j: colIndex}}/>
                      ))}
                  </div>
              ))}
          </div>

          <button onClick={startComputation}>Best Move</button>
      </div>
  )
}

export default App
