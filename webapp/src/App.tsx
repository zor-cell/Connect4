import './App.css'
import {compute} from './wasm/initWasm.js'

function App() {
  function startComputation() {
    compute()
        .then(res => {
            let arr: number[][] = JSON.parse(JSON.stringify(res));
            console.log(arr);
        }).catch(e => {
            console.log(e);
        });
  }

  return (
      <div>
        <button onClick={startComputation}>Button</button>
        <h1>Hello from React!</h1>
      </div>
  )
}

export default App
