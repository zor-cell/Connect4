self.importScripts("https://cjrtnc.leaningtech.com/3.0/cj3loader.js");

self.onmessage = async (event) => {
    console.log("in worker", event.data);

    await cheerpjInit();
    let lib = await cheerpjRunLibrary("/app/lib/Connect4Lib.jar");
    let connector = await lib.connect4.Connector;

    let config = event.data;
    const SolverConfigLib = await lib.connect4.data.SolverConfig;
    let serialized = await new SolverConfigLib(config.board, config.player, config.maxTime, config.maxDepth);

    const payload = await connector.makeBestMove(serialized);
    console.log(payload);

    self.postMessage(payload);
}
