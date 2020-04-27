function SSESubscription(url, eventType, eventHandler, path, history) {

    this.path = path;
    this.history = history;
    this.source = null;

    this.start = function () {
        console.log('SSESubscription start ' + url + ' : ' + eventType);
        this.source = new EventSource(url);

        this.source.addEventListener(eventType, function (event) {
            let eventJson = JSON.parse(event.data);
            console.log('Got update ' + url + ' : ' + eventType + ', ' +
                event.lastEventId + '. ' + JSON.stringify(eventJson));
            checkPathChange(eventJson);
        });

        this.source.onerror = function (event) {
            // this.close();
            console.log('Got update error ' + url + ' : ' + eventType + '. ' + event);
        };

    };

    this.stop = function () {
        console.log('SSESubscription stop ' + url + ' : ' + eventType);
        this.source.close();
    };

    function checkPathChange(eventJson) {
        if(path !== eventJson.path) {
            history.push({pathname: eventJson.path, state: eventJson})
        } else {
            eventHandler(eventJson);
        }
    }
}

export default SSESubscription;