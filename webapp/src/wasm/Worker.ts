import {WorkerRequest} from "./dtos/WorkerRequests.ts";
import {toast} from "react-toastify";

export let worker: Worker;
export function initWorker() {
    worker = new Worker("/workers/worker.js", { type: "classic" });

    let request: WorkerRequest = {
        type: "LOAD",
        data: null
    };
    worker.postMessage(request);
}

