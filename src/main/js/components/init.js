import  React, {useEffect} from "react";
import { useStoreActions } from "easy-peasy";
import Login from "./login";
import {Spinner, Button} from 'react-bootstrap'
const client = require('../client');

const Init = (props) => {

    const setGameId = useStoreActions(actions => actions.setGameId);
    const moveToGameProgressScreen = useStoreActions(actions => actions.moveToGameProgressScreen);
    const requestLogin = useStoreActions(actions => actions.requestLogin);

    function checkActiveGame() {
        client({method: 'GET', path: '/game'}).done(response => {
            const startDto = response.entity;
            if(startDto.isActive) {
                setGameId(startDto.lastGameId);
                moveToGameProgressScreen(props.history);
            } else {
                props.history.push({pathname: '/start', state: startDto.lastGameId})
            }
        }, (response) => {
            console.log('game error ' + JSON.stringify(response));
        });
    }

    useEffect(() => {
        console.log('componentDidMount');
        requestLogin();
        checkActiveGame();
    });

    return (
        <div>
            <Login/>
            <Button variant="primary" disabled>
                <Spinner
                    as="span"
                    animation="grow"
                    size="sm"
                    role="status"
                    aria-hidden="true"
                />
                Loading...
            </Button>
        </div>
    )
};

export default Init;