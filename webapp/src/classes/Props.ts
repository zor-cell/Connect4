import {Position} from "./Position.ts";

interface CellProps {
    cellValue: number;
    cellPosition: Position;
    currentPlayer: number;
    makeMove: (position: Position, player: number) => void;
}