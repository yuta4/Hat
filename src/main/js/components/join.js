import React, {useEffect, useState} from "react";
import {useStoreActions} from "easy-peasy";
import {Button, Label} from "semantic-ui-react";
import Login from "./login";

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

    const gamesToJoinSubscription = new gamesToJoinSubscriptionEvent();

    function gamesToJoinSubscriptionEvent () {

        this.source = null;

        this.start = function () {
            console.log('gamesToJoinSubscriptionEvent before EventSource');
            this.source = new EventSource("/game/notStartedEvents");

            this.source.onmessage = function (event) {
                console.log('gamesToJoinSubscriptionEvent onmessage for debug');
            };

            this.source.addEventListener("message", function (event) {
                console.log('Got update notStartedEvents ' + event);
                setGamesToJoin(JSON.parse(event.data));
            });

            this.source.onerror = function (event) {
                // this.close();
                console.log('Got update error ' + event);
            };

        };

        this.stop = function() {
            this.source.close();
        }

    }

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