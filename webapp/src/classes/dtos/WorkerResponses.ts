import {GameState} from "../GameState.ts";
import {Position} from "./Position.ts";

export interface WorkerResponse {
    type: 'LOAD' | 'UNDO' | 'MOVE' | 'BESTMOVE' | 'ERROR',
    data: ResponseData
}

export interface ResponseData {}

export interface ErrorResponse extends ResponseData {
    message: string
}

export interface MoveResponse extends ResponseData  {
     board: number[][],
     position: Position,
     gameState: GameState,
     score: number
}