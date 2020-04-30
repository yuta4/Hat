function checkPathChange(eventJson, path, history, eventHandler) {
    if(path !== eventJson.path) {
        history.push({pathname: eventJson.path, state: eventJson})
    } else {
        eventHandler(eventJson);
    }
}

export default checkPathChange;