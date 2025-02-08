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
    const position = deserializePosition(solverResponse.o.f2);
    const score = solverResponse.o.f3;
    const winDistance = solverResponse.o.f4;

    return {
        board: board,
        gameState: gameState,
        position: position,
        score: score,
        winDistance: winDistance
    }
}

function deserializeMoveResponse(moveResponse) {
    const board = deserializeBoard(moveResponse.o.f0);
    const position = deserializePosition(moveResponse.o.f1);
    const gameState = moveResponse.o.f2.f1;

    return {
        board: board,
        position: position,
        gameState: gameState
    }
}