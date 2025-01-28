import {FC} from "react";
import {CellProps} from "./classes/Props.ts"
import {Position} from "./classes/dtos/Position.ts";

const Cell: FC<CellProps> = ({cellValue, cellPosition, currentPlayer, lastMove, makeMove}) => {
    function startMakeMove(position: Position, player: number) {
        makeMove(position, player);
    }

    function getCellFromPlayer(player: number) {
        if(player === 1) {
            return 'red-circle';
        } else if(player === -1) {
            return 'yellow-circle';
        } else {
           return 'white-circle';
        }
    }

    return (
        <div className={`${lastMove != null && lastMove.i == cellPosition.i && lastMove.j == cellPosition.j ? 'last-move' : ''} board-cell`} onClick={() => startMakeMove(cellPosition, currentPlayer)}>
            <div className={getCellFromPlayer(cellValue)}></div>
        </div>
    )
}

export default Cell;