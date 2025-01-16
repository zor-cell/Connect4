export class MovePayload {
    public board: number[][];
    public moveI: number;
    public moveJ: number;

    constructor(board: number[][], moveI: number, moveJ: number) {
        this.board = board;
        this.moveI = moveI;
        this.moveJ = moveJ;
    }

    public static parse(payload: any): MovePayload {
        let objArr: object[] = JSON.parse(JSON.stringify(payload.board));
        const board: number[][] = objArr.map(obj => Object.values(obj));

        let moveI: number = payload.moveI;
        let moveJ: number = payload.moveJ;

        return new MovePayload(board, moveI, moveJ);
    }
}