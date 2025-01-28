importScripts("https://cjrtnc.leaningtech.com/3.0/cj3loader.js");

self.onmessage = async (event) => {
    console.log("in worker", event.data);

    await cheerpjInit();

    let config = event.data;
    /*onnector.computeBestMove(config)
        .then(payload => {
            console.log(payload)
            self.postMessage('success');
        }).catch(err => {
        self.postMessage(err);
    });*/
}