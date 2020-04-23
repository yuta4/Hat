function SSESubscription(url, eventType, eventHandler) {

    this.source = null;

    this.start = function () {
        console.log('SSESubscription start ' + url + ' : ' + eventType);
        this.source = new EventSource(url);

        this.source.addEventListener(eventType, function (event) {
            let eventJson = JSON.parse(event.data);
            console.log('Got update ' + url + ' : ' + eventType + ', ' +
                event.lastEventId + '. ' + JSON.stringify(eventJson));
            eventHandler(eventJson);
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
}

export default SSESubscription;