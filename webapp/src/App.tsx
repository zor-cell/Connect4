import './App.css'
import {useEffect, useRef, useState} from "react";
import Cell from "./components/Cell.tsx";
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
import PlayerSettings from "./components/PlayerSettings.tsx";
import {Player} from "./classes/Player.ts";

const loadingToastId = toast.loading("Loading resources...");
initWorker();

let isLoading: boolean = true;
function App() {
    const [player1, setPlayer1] = useState<Player | null>(null);
    const [player2, setPlayer2] = useState<Player | null>(null);
    const [currentPlayer, setCurrentPlayer] = useState(1);
    const [lastAiPlayer, setLastAiPlayer] = useState<number | null>(null);

    const [isLoadingState, setIsLoadingState] = useState(isLoading);
    const [board, setBoard] = useState(createBoard(6, 7));
    const [gameOver, setGameOver] = useState(false);
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

                setBoard(solverResponse.board);
                setMoves(prev => [...prev, solverResponse.position]);
                setGameState(solverResponse.gameState);
                setScore(solverResponse.score);
                setWinDistance(solverResponse.winDistance);
                setLastAiPlayer(currentPlayer);
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
            let winner = gameState === GameState.PLAYER1 ? "Red" : "Yellow";
            toast.info(`Player ${winner} won!`);
            setGameOver(true);
        } else {
            setGameOver(false);
        }
    }, [gameState])

    //trigger ai move
    useEffect(() => {
        if(player1 == null || player2 == null) return;

        //dont start ai move when game is over
        if(gameState != GameState.RUNNING) return;

        //dont start ai move on undo
        const currentPieceCount = countPieces(board);
        const previousPieceCount = prevBoardRef.current;
        prevBoardRef.current = currentPieceCount;
        if(currentPieceCount < previousPieceCount) return;

        //start ai move
        if(currentPlayer == 1 && player1.isAi) {
            startBestMove(currentPlayer, player1);
        } else if(currentPlayer == -1 && player2.isAi) {
            startBestMove(currentPlayer, player2);
        }
    }, [currentPlayer]);

    function togglePlayer() {
        setCurrentPlayer(prev => prev === 1 ? -1 : 1);
    }

    function abortWorker() {
        worker.terminate();
        initWorker();

        isLoading = false;
        setIsLoadingState(false);

        if(currentPlayer == -1) {
            //startUndoMove();
        }
    }

    function refresh() {
        setBoard(createBoard(6, 7));
        setGameOver(false);
        setCurrentPlayer(1);
        setGameState(GameState.RUNNING);
        setMoves(new Array<Position>);
        setScore(0);
        setWinDistance(-1);
        prevBoardRef.current = 0;
    }

    function startBestMove(player: number, playerSettings: Player) {
        if(gameOver || isLoading) return;
        isLoading = true;
        setIsLoadingState(true);

        const solverRequest: SolverRequest = {
            board: board,
            player: player,
            maxTime: playerSettings.maxTime,
            maxDepth: -1,
            tableSize: playerSettings.maxMemory
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

    function getWinner() {
        if(lastAiPlayer == null) return;

        if(lastAiPlayer == 1) {
            if(score > 0) return 1;
            else if(score < 0) return -1;
        } else if(lastAiPlayer == -1) {
            if(score > 0) return -1;
            else if(score < 0) return 1;
        }

        return 0;
    }

    return (
        <div id="container" className="flex-container">
            <div id="settings-container" className="flex-container">
                <PlayerSettings
                    color={"Red"}
                    defaultIsAi={false}
                    hasStart={currentPlayer == 1 && isLoading == false && !gameOver}
                    setPlayer={setPlayer1}
                    onStart={() => startBestMove(1, player1)}/>
                <PlayerSettings
                    color={"Yellow"}
                    defaultIsAi={true}
                    hasStart={currentPlayer == -1 && isLoading == false && !gameOver}
                    setPlayer={setPlayer2}
                    onStart={() => startBestMove(-1, player2)}/>
            </div>

            <div id="board-container">
                <div id="buttons" className="flex-container mt-3 mb-1">
                    <div>
                        {isLoadingState && <button className="btn btn-danger" onClick={abortWorker}> {/*TODO: only show abort when ai is thinking*/}
                            <i className="bi bi-x-circle"></i> Abort
                        </button>}
                        {!isLoadingState && <button className="btn btn-primary" onClick={startUndoMove}>
                            <i className="bi bi-arrow-left-circle"></i> Undo
                        </button>}
                    </div>
                    <button className="btn btn-success" onClick={refresh} disabled={isLoadingState}>
                        <i className="bi bi-arrow-repeat"></i> New
                    </button>
                </div>
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
            </div>

            {<div className="flex-container mt-3 mb-4">
                <p className="m-0"><b>Score:</b> {score}</p>
                {winDistance >= 0 && <p className="m-0">Player {getWinner() == 1 ? "Red" : "Yellow"} wins
                    in {Math.ceil(winDistance / 2)} moves!</p>}
            </div>}

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