import React from 'react';
import { useStoreActions } from "easy-peasy";
import Login from "./login";
const client = require('../client');

const NewGame = (props) => {

    const moveToGameProgressScreen = useStoreActions(actions => actions.moveToGameProgressScreen);
    const setGamesToJoin = useStoreActions(actions => actions.setGamesToJoin);
    const setGameId = useStoreActions(actions => actions.setGameId);

    function createGame() {
        client({method: 'POST', path: '/game/create'}).done(response => {
            console.log('NewGame ' + response.entity);
            setGameId(response.entity);
            moveToGameProgressScreen(props.history)
        });
    }

    function moveToJoinGameOption() {
        client({method: 'GET', path: '/game/notStarted'}).done(response => {
            console.log('moveToJoinGameOption ' + response.entity);
            setGamesToJoin(response.entity);
            props.history.push({pathname: "/join"});
        }, (response) => {
            console.log('moveToJoinGameOption ex')
        });

    }

    return (
        <div>
            <Login/>
            <h1>NewGameScreen</h1>
            <button onClick={createGame}>Create new game</button>
            <button onClick={moveToJoinGameOption}>Join existing one</button>
        </div>
    )
};

export default NewGame;