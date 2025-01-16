let lib = undefined;

export async function loadLibrary(): Promise<void> {
    //initialise cheerPJ
    await cheerpjInit();

    //load compiled .jar library
    lib = await cheerpjRunLibrary("/app/src/wasm/lib/JavaWebAssembly.jar");
    console.log("java library loaded")
}

export async function computeBestMove(board: number[][]): Promise<Payload> {
    if(lib === undefined) {
        throw "java library not loaded yet";
    }

    //call java
    const Connector: any = await lib.connect4.Connector;
    const payload = await Connector.computeBestMove(board);

    return parsePayload(payload);
}

export async function makeMove(board: number[][], player: number, position: Position): Promise<Payload> {
    if(lib === undefined) {
        throw "java library not loaded yet";
    }

    //call java
    const Connector: any = await lib.connect4.Connector;
    const payload = await Connector.makeMove(board, player, position.i, position.j);

    return parsePayload(payload);
}

function parsePayload(javaPayload: any): Payload {
    let objArr: object[] = JSON.parse(JSON.stringify(javaPayload.o.f0));
    const board: number[][] = objArr.map(obj => Object.values(obj));

    return {board: board};
}