import {Version} from "./Version.ts";

export interface Player {
    isAi: boolean;
    maxTime: number;
    maxMemory: number;
    version: Version;
}