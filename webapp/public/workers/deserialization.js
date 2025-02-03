function deserializeBoard(board) {
    const objArr = JSON.parse(JSON.stringify(board));
    return objArr.map(obj => Object.values(obj));
}

function deserializePosition(position) {
    return {
        i: position.f0,
        j: position.f1
    };
}

function deserializeSolverResponse(solverResponse) {
    const board = deserializeBoard(solverResponse.o.f0);
    const gameState = solverResponse.o.f1.f1;
    const bestMove = {
        position: deserializePosition(solverResponse.o.f2.f0),
        score: solverResponse.o.f2.f1,
        winDistance: solverResponse.o.f2.f2
    }

    return {
        board: board,
        gameState: gameState,
        bestMove: bestMove
    }
}

function deserializeMoveResponse(moveResponse) {
    const board = deserializeBoard(moveResponse.o.f0);
    const position = {
        i: moveResponse.o.f1.f0,
        j: moveResponse.o.f1.f1
    };
    const gameState = moveResponse.o.f2.f1;

    return {
        board: board,
        position: position,
        gameState: gameState
    }
}