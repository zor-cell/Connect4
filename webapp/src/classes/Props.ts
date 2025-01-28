import {Position} from "./dtos/Position.ts";

export interface CellProps {
    cellValue: number;
    cellPosition: Position;
    currentPlayer: number;
    lastMove: Position;
    makeMove: (position: Position, player: number) => void;
}