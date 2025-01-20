export class SolverConfig {
    public board: number[][];
    public player: number;
    public maxTime: number;
    public maxDepth: number;

    constructor(board: number[][], player: number, maxTime: number, maxDepth: number) {
        this.board = board;
        this.player = player;
        this.maxTime = maxTime;
        this.maxDepth = maxDepth;
    }

    public async serialize(lib: any): Promise<any> {
        const SolverConfigLib: any = await lib.connect4.data.SolverConfig;

        return await new SolverConfigLib(this.board, this.player, this.maxTime, this.maxDepth);
    }
}