import React from 'react';
import { useStoreActions } from "easy-peasy";
const client = require('../client');

const NewGame = () => {

    function createGame() {
        const moveToGameProgressScreen = useStoreActions(actions => actions.moveToGameProgressScreen);

        client({method: 'POST', path: '/game/create'}).done(response => {
            console.log('NewGame ' + response.entity);
            moveToGameProgressScreen(response.entity, this.props.history)
        });
    }

    return (
        <div>
            <h1>NewGameScreen</h1>
            <button onClick={createGame}>Create new game</button>
        </div>
    )
};

export default NewGame;