import {Position} from "../wasm/dtos/Position.ts";

export interface CellProps {
    cellValue: number;
    cellPosition: Position;
    currentPlayer: number;
    makeMove: (position: Position, player: number) => void;
}