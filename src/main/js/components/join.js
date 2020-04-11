import React, {useEffect} from "react";
import {useStoreActions, useStoreState} from "easy-peasy";
import {Button, Label} from "semantic-ui-react";

const JoinScreen = (props) => {

    const gamesToJoin = useStoreState(state => state.games_to_join);
    const moveToGameProgressScreen = useStoreActions(actions => actions.moveToGameProgressScreen);
    const setGameId = useStoreActions(actions => actions.setGameId);


    useEffect(() => {
        console.log('JoinScreen useEffect' + {gamesToJoin});
    });

    function joinGame(id) {
        setGameId(id);
        moveToGameProgressScreen(props.history);
    }

    return (
        <div>
            <h1>Join</h1>
            {gamesToJoin.map(game => (
                <div key={game.gameId}>
                    <Button onClick={() => joinGame(game.gameId)}>Join</Button>
                    <Label color='blue' horizontal>{game.gameId}</Label>
                    <Label color='yellow' horizontal>{game.login}</Label>
                </div>
            ))}
        </div>
    )
};

export default JoinScreen;