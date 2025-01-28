import {GameState} from "../../classes/GameState.ts";
import {Position} from "./Position.ts";

export interface WorkerResponse {
    type: 'LOAD' | 'BESTMOVE',
    data: MoveResponse
}

export interface MoveResponse {
     board: number[][],
     position: Position,
     gameState: GameState,
     score: number
}