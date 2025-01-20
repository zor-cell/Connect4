import {GameState} from "../../classes/GameState.ts";
import {Position} from "./Position.ts";

export class MovePayload {
    public board: number[][];
    public position: Position;
    public gameState: GameState;
    public score: number;

    constructor(board: number[][], position: Position, gameState: GameState, score: number = 0) {
        this.board = board;
        this.position = position;
        this.gameState = gameState;
        this.score = score;
    }

    public static deserialize(payload: any): MovePayload {
        let objArr: object[] = JSON.parse(JSON.stringify(payload.o.f0));
        const board: number[][] = objArr.map(obj => Object.values(obj));

        let position: Position = new Position(payload.o.f1.f0, payload.o.f1.f1);
        let gameState: GameState = payload.o.f2.f1 as GameState;
        let score: number = payload.o.f3;

        return new MovePayload(board, position, gameState, score);
    }
}