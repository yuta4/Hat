import {action, thunk} from "easy-peasy";
const client = require('./client');

export default {
    //state
    login:'',
    gid: null,
    owner: '',
    path: '',
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
    })
};