importScripts("https://cjrtnc.leaningtech.com/3.0/cj3loader.js");

self.onmessage = (event) => Worker.handleMessage(event);

class Worker {
    static loaded = false;
    static lib;
    static Connector;
    static SolverRequest;
    static MoveRequest;
    static UndoRequest;
    static Position;

    static async handleMessage(event) {
        //console.log("worker received", event.data);

        try {
            let request = event.data.data;
            switch (event.data.type) {
                case 'LOAD':
                    await this.loadLibrary();
                    break;
                case 'BESTMOVE':
                    await this.makeBestMove(request);
                    break;
                case 'MOVE':
                    await this.makeMove(request);
                    break;
                case 'UNDO':
                    await this.undoMove(request);
                    break;
            }
        } catch(error) {
            self.postMessage({
                type: 'ERROR',
                data: error.message
            })
        }
    }

    static async loadLibrary() {
        //initialise cheerPJ
        await cheerpjInit();

        //load compiled .jar library
        this.lib = await cheerpjRunLibrary("/app/lib/Connect4Lib.jar");

        //preload needed instance classes
        this.Connector = await this.lib.connect4.Connector;
        this.SolverRequest = await this.lib.connect4.data.requests.SolverRequest;
        this.MoveRequest = await this.lib.connect4.data.requests.MoveRequest;
        this.UndoRequest = await this.lib.connect4.data.requests.UndoRequest;
        this.Position = await this.lib.connect4.data.Position;

        this.loaded = true;

        self.postMessage({
            type: 'LOAD',
            data: null
        });
    }

    static async makeBestMove(request) {
        this.checkValidLib();

        const solverRequest = await new this.SolverRequest(request.board, request.player, request.maxTime, request.maxDepth);

        const payload = await this.Connector.makeBestMove(solverRequest);

        const solverResponse = this.deserializeResponse(payload);

        self.postMessage({
            type: 'BESTMOVE',
            data: solverResponse
        });
    }

    static async makeMove(request) {
        this.checkValidLib();

        const position = await new this.Position(request.position.i, request.position.j);
        const moveRequest = await new this.MoveRequest(request.board, request.player, position);

        const payload = await this.Connector.makeMove(moveRequest);

        const moveResponse = this.deserializeResponse(payload);

        self.postMessage({
            type: 'MOVE',
            data: moveResponse
        })
    }

    static async undoMove(request) {
        this.checkValidLib();

        const position = await new this.Position(request.position.i, request.position.j);
        const undoRequest = await new this.UndoRequest(request.board, position);

        const payload = await this.Connector.undoMove(undoRequest);

        const undoResponse = this.deserializeResponse(payload);

        self.postMessage({
            type: 'UNDO',
            data: undoResponse
        })
    }

    static checkValidLib() {
        if(!this.loaded) {
            throw new Error("Not all resources have been loaded yet!");
        }
    }

    static deserializeResponse(payload) {
        let objArr = JSON.parse(JSON.stringify(payload.o.f0));
        const board = objArr.map(obj => Object.values(obj));

        let position = {
            i: payload.o.f1.f0,
            j: payload.o.f1.f1
        };
        let gameState = payload.o.f2.f1;
        let score = payload.o.f3;

        return {
            board: board,
            position: position,
            gameState: gameState,
            score: score
        };
    }
}