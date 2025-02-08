import {GameState} from "../GameState.ts";
import {Position} from "./Position.ts";
import {BestMove} from "./BestMove.ts";

export interface WorkerResponse {
    type: 'LOAD' | 'UNDO' | 'MOVE' | 'BESTMOVE' | 'ERROR',
    data: ResponseData
}

export interface ResponseData {}

export interface ErrorResponse extends ResponseData {
    message: string
}

export interface SolverResponse extends ResponseData {
    board: number[][],
    gameState: GameState,
    position: Position,
    score: number,
    winDistance: number
}

export interface MoveResponse extends ResponseData  {
     board: number[][],
     position: Position,
     gameState: GameState
}