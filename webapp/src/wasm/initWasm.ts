let lib = undefined;

export async function loadLibrary(): Promise<void> {
    //initialise cheerPJ
    await cheerpjInit();

    //load compiled .jar library
    lib = await cheerpjRunLibrary("/app/src/wasm/lib/JavaWebAssembly.jar");
    console.log("java library loaded")
}

export async function computeBestMove(): Promise<Payload> {
    if(lib === undefined) {
        throw "java library not loaded yet";
    }

    //call computation
    const Connector: any = await lib.connect4.Connector;
    const payload = await Connector.computeBestMove();

    //parse payload members to ts object
    let objArr: object[] = JSON.parse(JSON.stringify(payload.o.f0));
    const board: number[][] = objArr.map(obj => Object.values(obj))

    return {
        board: board
    };
}

export async function makeMove(position: Position): Promise<Payload> {
    if(lib === undefined) {
        throw "java library not loaded yet";
    }

    return {board: undefined};
}