import {action, thunk} from "easy-peasy";
import checkPathChange from "./pathChecker";
const client = require('./client');

export default {
    //state
    login:'',
    gid: null,
    eventSources: new Map(),
    //thunk
    requestLogin: thunk((actions) => {
        client({method: 'GET', path: '/game/login'}).done(response => {
            actions.setLogin(response.entity.login);
        });
    }),
    moveToGameProgressScreen: thunk((actions, history, helpers) => {
        const gid = helpers.getStoreState().gid;
        client({method: 'PUT', path: '/progress?gameId=' + gid}).done(response => {
            console.log('moveToGameProgressScreen ' + response.entity + gid);
            history.push({pathname: response.entity.path, state: response.entity})
        }, response => {
            console.log('moveToGameProgressScreen error ' + response.status);
        });
    }),
    moveToJoinGameOption: thunk((actions, history) => {
        client({method: 'GET', path: '/game/notStarted'}).done(response => {
            console.log('moveToJoinGameOption ' + response.entity);
            history.push({pathname: "/join", state: response.entity});
        }, (response) => {
            console.log('moveToJoinGameOption ex')
        });
    }),
    //action
    setGameId: action((state, id) => {
        state.gid = id
    }),
    setLogin: action((state, login) => {
       state.login = login;
    }),
    addEventListener: action((state, payload) => {
        let es = state.eventSources.get(payload.url);
        let eventSource;
        if(es === undefined) {
            eventSource = new EventSource(payload.url + '/' + state.login);
            es = { source : eventSource, events: new Map()};
            state.eventSources.set(payload.url, es);
        } else {
            eventSource = es.source;
        }
        eventSource.addEventListener(payload.eventType,
            function (event) {
                checkPathChange(JSON.parse(event.data), payload.path,
                    payload.history, payload.handler);
            });
        console.log('added listener for ' + payload.url + ', ' + payload.eventType + 'readyState = ' + eventSource.readyState);
        es.events.set(payload.eventType, payload.handler);
    }),
    removeEventListener: action((state, payload) => {
        let es = state.eventSources.get(payload.url);
        if(es !== undefined) {
            let eventSource = es.source;
            const handler = es.events.get(payload.eventType);
            eventSource.removeEventListener(payload.eventType, handler);
            console.log('removed listener for ' + payload.url + ', ' + payload.eventType + 'readyState = ' + eventSource.readyState);
            es.events.delete(payload.eventType);
        }
    })
};