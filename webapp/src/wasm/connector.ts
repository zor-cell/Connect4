import {MovePayload} from "./dtos/MovePayload.ts";
import {toast} from "react-toastify";
import {Position} from "./dtos/Position.ts";
import {SolverConfig} from "./dtos/SolverConfig.ts";

export class Connector {
    private static lib: any;
    private static connector: any;

    public static async loadLibrary(): Promise<void> {
        //initialise cheerPJ
        await cheerpjInit();

        //load compiled .jar library
        const id = toast.loading("Loading resources...");
        this.lib = await cheerpjRunLibrary("/app/lib/Connect4Lib.jar");
        this.connector = await this.lib.connect4.Connector;
        toast.update(id, {render: "Resources loaded!", type: "success", isLoading: false, autoClose: 5000});
    }

    public static async computeBestMove(config: SolverConfig): Promise<MovePayload> {
        this.checkValidLib();

        const configSerialized = await config.serialize(this.lib);
        const payload: any = await this.connector.makeBestMove(configSerialized);

        return MovePayload.deserialize(payload);
    }

    public static async makeMove(board: number[][], player: number, position: Position): Promise<MovePayload> {
        this.checkValidLib();

        const payload: any = await this.connector.makeMove(board, player, position.j);

        return MovePayload.deserialize(payload);
    }

    public static async undoMove(board: number[][], position: Position): Promise<MovePayload> {
        this.checkValidLib();

        const posSerialized = await position.serialize(this.lib);
        const payload: any = await this.connector.undoMove(board, posSerialized);

        return MovePayload.deserialize(payload);
    }

    private static checkValidLib(): void {
        if(this.lib === undefined) {
            throw "Not all resources have been loaded yet!";
        }
    }
}