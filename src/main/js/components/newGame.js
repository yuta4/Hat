import React from 'react';
import { useStoreActions } from "easy-peasy";
import Login from "./login";
const client = require('../client');

const NewGame = (props) => {

    const moveToGameProgressScreen = useStoreActions(actions => actions.moveToGameProgressScreen);
    const moveToJoinGameOption = useStoreActions(actions => actions.moveToJoinGameOption);
    const setGameId = useStoreActions(actions => actions.setGameId);

    function createGame() {
        client({method: 'POST', path: '/game/create'}).done(response => {
            console.log('NewGame ' + response.entity);
            setGameId(response.entity);
            moveToGameProgressScreen(props.history)
        });
    }

    return (
        <div>
            <Login/>
            <h1>NewGameScreen</h1>
            <button onClick={createGame}>Create new game</button>
            <button onClick={() => moveToJoinGameOption(props.history)}>Join existing one</button>
        </div>
    )
};

export default NewGame;