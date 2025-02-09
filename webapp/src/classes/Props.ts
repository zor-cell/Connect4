import {Position} from "./dtos/Position.ts";

export interface CellProps {
    cellValue: number;
    cellPosition: Position;
    currentPlayer: number;
    lastMove: Position;
    makeMove: (position: Position, player: number) => void;
}

export interface SliderCheckBoxProps {
    isChecked: boolean;
    setIsChecked: any;
}

export interface PlayerSettingsProps {
    color: string;
    defaultIsAi: boolean;
    hasStart: boolean;
    setPlayer: any;
    onStart: any;
}