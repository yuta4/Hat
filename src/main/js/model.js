import {action, thunk} from "easy-peasy";
const client = require('./client');

export default {
    //state
    gid: null,
    owner: '',
    path: '',
    path_data: [],
    game_players: [],
    game_watchers: [],
    //thunk
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
        state.game_players = entity.game_players !== undefined ? entity.game_players : [];
        state.game_watchers = entity.game_watchers !== undefined ? entity.game_watchers : [];
    })
};