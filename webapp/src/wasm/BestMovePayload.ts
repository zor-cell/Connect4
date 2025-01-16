export class BestMovePayload {
    public board: number[][];

    constructor(board: number[][]) {
        this.board = board;
    }

    public static parse(payload: any): BestMovePayload {
        let objArr: object[] = JSON.parse(JSON.stringify(payload.board));
        const board: number[][] = objArr.map(obj => Object.values(obj));

        return new BestMovePayload(board);
    }
}