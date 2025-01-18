import {BestMovePayload} from "./dtos/BestMovePayload.ts";
import {MovePayload} from "./dtos/MovePayload.ts";
import {toast} from "react-toastify";


let lib = undefined;

export async function loadLibrary(): Promise<void> {
    //initialise cheerPJ
    await cheerpjInit();

    const id = toast.loading("Loading resources...");

    //load compiled .jar library
    lib = await cheerpjRunLibrary("/app/src/wasm/lib/JavaWebAssembly.jar");
    toast.update(id, {render: "Resources loaded!", type: "success", isLoading: false, autoClose: 5000});
}

export async function computeBestMove(board: number[][], player: number): Promise<BestMovePayload> {
    if(lib === undefined) {
        throw "Not all resources have been loaded yet!";
    }

    //call java
    const Connector: any = await lib.connect4.Connector;
    const payload: any = await Connector.makeBestMove(board, player);

    return BestMovePayload.parse(payload);
}

export async function makeMove(board: number[][], player: number, position: Position): Promise<MovePayload> {
    if(lib === undefined) {
        throw "Not all resources have been loaded yet!";
    }

    //call java
    const Connector: any = await lib.connect4.Connector;
    const payload: any = await Connector.makeMove(board, player, position.j);

    console.log(payload)
    return MovePayload.deserialize(payload);
}