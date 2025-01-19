import {Position} from "./Position.ts";

export interface CellProps {
    cellValue: number;
    cellPosition: Position;
    currentPlayer: number;
    makeMove: (position: Position, player: number) => void;
}