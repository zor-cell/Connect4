export class MovePayload {
    public board: number[][];
    public moveI: number;
    public moveJ: number;
    public gameState: number;

    constructor(board: number[][], moveI: number, moveJ: number, gameState: number) {
        this.board = board;
        this.moveI = moveI;
        this.moveJ = moveJ;
        this.gameState = gameState;
    }

    public static parse(payload: any): MovePayload {
        let objArr: object[] = JSON.parse(JSON.stringify(payload.o.f0));
        const board: number[][] = objArr.map(obj => Object.values(obj));

        let moveI: number = payload.o.f1;
        let moveJ: number = payload.o.f2;
        let gameState: number = payload.o.f3;

        return new MovePayload(board, moveI, moveJ, gameState);
    }
}