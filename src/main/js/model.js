import {action, thunk} from "easy-peasy";
const client = require('./client');

export default {
    //state
    login:'',
    gid: null,
    owner: '',
    path: '',
    path_data: [],
    game_players: [],
    game_watchers: [],
    games_to_join: [],
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
            actions.setGameProgressFromJson(response.entity);
            history.push({pathname: response.entity.path})
        }, response => {
            console.log('moveToGameProgressScreen error ' + response.status);
        });
    }),
    //action
    setGameId: action((state, id) => {
        state.gid = id
    }),
    setGameProgressFromJson: action((state, entity) => {
        state.owner = entity.owner;
        state.path = entity.path;
        state.path_data = entity.path_data !== undefined ? entity.path_data : [];
        state.game_players = entity.players !== undefined ? entity.players : [];
        state.game_watchers = entity.watchers !== undefined ? entity.watchers : [];
    }),
    setGamesToJoin: action((state, games) => {
        state.games_to_join = games !== undefined ? games : [];
    }),
    setLogin: action((state, login) => {
       state.login = login;
    })
};