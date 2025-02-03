import {Position} from "./Position.ts";

export interface BestMove {
    position: Position,
    score: number,
    winDistance: number
}