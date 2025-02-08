import {Position} from "./Position.ts";

export interface WorkerRequest {
    type: 'LOAD' | 'UNDO' | 'MOVE' | 'BESTMOVE',
    data: RequestData
}

export interface RequestData {}

export interface SolverRequest extends RequestData {
    board: number[][],
    player: number,
    maxTime: number,
    maxDepth: number,
    tableSize: number
}

export interface MoveRequest extends RequestData {
    board: number[][],
    player: number,
    position: Position
}

export interface UndoRequest extends RequestData {
    board: number[][],
    position: Position
}