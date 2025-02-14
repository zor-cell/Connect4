importScripts(
    "https://cjrtnc.leaningtech.com/3.0/cj3loader.js",
    "deserialization.js"
);

self.onmessage = (event) => Worker.handleMessage(event);

class Worker {
    static loaded = false;
    static lib;

    static Connector;
    static Position;

    static SolverRequest;
    static MoveRequest;
    static UndoRequest;

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
            console.error(error)
            self.postMessage({
                type: 'ERROR',
                data: error.message ? error.message : error
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
        this.Position = await this.lib.connect4.data.Position;
        this.SolverRequest = await this.lib.connect4.data.requests.SolverRequest;
        this.MoveRequest = await this.lib.connect4.data.requests.MoveRequest;
        this.UndoRequest = await this.lib.connect4.data.requests.UndoRequest;
        this.Version = await this.lib.connect4.data.Version;

        this.loaded = true;

        self.postMessage({
            type: 'LOAD',
            data: null
        });
    }

    static mapVersion(version) {
        if(version === 0) {
            return this.Version.V1_0;
        } else if(version === 1) {
            return this.Version.V1_1;
        } else if(version === 2) {
            return this.Version.V2_0;
        } else if(version === 3) {
            return this.Version.V2_1;
        }
    }

    static async makeBestMove(request) {
        this.checkValidLib();

        const version = this.mapVersion(request.version);
        const solverRequest = await new this.SolverRequest(request.board, request.player, request.maxTime, request.maxDepth, request.tableSize, version);
        const solverResponse = await this.Connector.makeBestMove(solverRequest);

        self.postMessage({
            type: 'BESTMOVE',
            data: deserializeSolverResponse(solverResponse)
        });
    }

    static async makeMove(request) {
        this.checkValidLib();

        const requestPosition = await new this.Position(request.position.i, request.position.j);
        const moveRequest = await new this.MoveRequest(request.board, request.player, requestPosition);
        const moveResponse = await this.Connector.makeMove(moveRequest);

        self.postMessage({
            type: 'MOVE',
            data: deserializeMoveResponse(moveResponse)
        })
    }

    static async undoMove(request) {
        this.checkValidLib();

        const requestPosition = await new this.Position(request.position.i, request.position.j);
        const undoRequest = await new this.UndoRequest(request.board, requestPosition);
        const undoResponse = await this.Connector.undoMove(undoRequest);

        self.postMessage({
            type: 'UNDO',
            data: deserializeMoveResponse(undoResponse)
        })
    }

    static checkValidLib() {
        if(!this.loaded) {
            throw new Error("Not all resources have been loaded yet!");
        }
    }
}