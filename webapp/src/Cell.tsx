import {FC} from "react";

const Cell: FC<CellProps> = ({cellValue, cellPosition, currentPlayer, makeMove}) => {
    function startMakeMove(position: Position, player: number) {
        makeMove(position, player);
    }

    function getCellFromPlayer(player: number) {
        if(player === 1) {
            return 'red-circle';
        } else if(player === 2) {
            return 'yellow-circle';
        } else {
           return 'white-circle';
        }
    }

    return (
        <div className="board-cell" onClick={() => startMakeMove(cellPosition, currentPlayer)}>
            <div className={getCellFromPlayer(cellValue)}></div>
        </div>
    )
}

export default Cell;