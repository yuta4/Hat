import React, {useEffect, useState} from "react";
import {useStoreActions} from "easy-peasy";
import {Button, Label} from "semantic-ui-react";
import Login from "./login";
import SSESubscription from "../sseSubscription";

const JoinScreen = (props) => {

    const [gamesToJoin, setGamesToJoin] = useState(props.location.state);
    const moveToGameProgressScreen = useStoreActions(actions => actions.moveToGameProgressScreen);
    const setGameId = useStoreActions(actions => actions.setGameId);

    useEffect(() => {
        console.log('JoinScreen useEffect' + {gamesToJoin});
        gamesToJoinSubscription.start();

        return () => {
            console.log('JoinScreen useEffect close');
            gamesToJoinSubscription.stop();
        }
    });

    function joinGame(id) {
        setGameId(id);
        moveToGameProgressScreen(props.history);
    }

    function toMain() {
        props.history.push({pathname: '/'});
    }

    const gamesToJoinSubscription = new SSESubscription("/game/notStartedEvents", "message", setGamesToJoin);

    return (
        <div>
            <Login/>
            <h1>Join</h1>
            <Button onClick={() => toMain()}>To main menu</Button>
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