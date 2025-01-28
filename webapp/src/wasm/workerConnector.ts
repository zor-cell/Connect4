import {WorkerRequest} from "./dtos/WorkerRequests.ts";

export const worker: Worker = new Worker("/workers/worker.js", { type: "classic" });

let request: WorkerRequest = {
    type: "LOAD",
    data: null
};
worker.postMessage(request);