import {BestMovePayload} from "./BestMovePayload.ts";
import {MovePayload} from "./MovePayload.ts";


let lib = undefined;

export async function loadLibrary(): Promise<void> {
    //initialise cheerPJ
    await cheerpjInit();

    //load compiled .jar library
    lib = await cheerpjRunLibrary("/app/src/wasm/lib/JavaWebAssembly.jar");
    console.log("java library loaded")
}

export async function computeBestMove(board: number[][], player: number): Promise<BestMovePayload> {
    if(lib === undefined) {
        throw "java library not loaded yet";
    }

    //call java
    const Connector: any = await lib.connect4.Connector;
    const payload = await Connector.makeBestMove(board, player);

    console.log(payload)
    return BestMovePayload.parse(payload);
}

export async function makeMove(board: number[][], player: number, position: Position): Promise<MovePayload> {
    if(lib === undefined) {
        throw "java library not loaded yet";
    }

    //call java
    const Connector: any = await lib.connect4.Connector;
    const payload = await Connector.makeMove(board, player, position.i, position.j);

    return MovePayload.parse(payload);
}