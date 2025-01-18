export class Position {
    public i: number;
    public j: number;

    constructor(i: number, j: number) {
        this.i = i;
        this.j = j;
    }

    public async serialize(lib: any): any {
        const PositionLib: any = await lib.connect4.data.Position;

        return await new PositionLib(this.i, this.j);
    }
}