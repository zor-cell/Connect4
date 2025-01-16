interface CellProps {
    cellValue: number;
    cellPosition: Position;
    makeMove: (position: Position) => void;
}