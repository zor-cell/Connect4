let lib = undefined;

export async function loadLibrary() {
    //initialise cheerPJ
    await cheerpjInit();

    //load compiled .jar library
    lib = await cheerpjRunLibrary("/app/src/wasm/lib/JavaWebAssembly.jar");
    console.log("java library loaded")
}

export async function compute() {
    if(lib === undefined) {
        throw "java library not loaded yet";
    }

    //call computation
    const Connector = await lib.connect4.Connector;
    return await Connector.compute();
}