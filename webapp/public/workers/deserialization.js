function deserializeBoard(board) {
    const objArr = JSON.parse(JSON.stringify(board));
    return objArr.map(obj => Object.values(obj));
}

function deserializeSolverResponse(solverResponse) {
    const board = deserializeBoard(solverResponse.o.f0);
    const position = {
        i: solverResponse.o.f1.f0,
        j: solverResponse.o.f1.f1
    };
    const gameState = solverResponse.o.f2.f1;
    const score = solverResponse.o.f3;

    return {
        board: board,
        position: position,
        gameState: gameState,
        score: score
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