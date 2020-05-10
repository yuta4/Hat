import React, {useState} from 'react';
import {useStoreActions} from "easy-peasy";
import Login from "./login";
import {Button, Header, Icon, Segment} from "semantic-ui-react";
import logo from '../images/hat.png';

const client = require('../client');

const Start = (props) => {

    const [lastGame, setLastGame] = useState(props.location.state);
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

    function moveToLastGame() {
        setGameId(lastGame);
        moveToGameProgressScreen(props.history);
    }

    return (
        <div>
            <Segment clearing secondary>
                <Login/>
                <Header image={logo}
                        as='h1' textAlign='center'>
                    <Header.Content>Hat online</Header.Content>
                </Header>
                <Button.Group attached='bottom'>
                    <Button onClick={createGame} color='green' icon='file outline' content='Create'/>
                    <Button.Or/>
                    <Button onClick={() => moveToJoinGameOption(props.history)} color='pink' icon='fork'
                            content='Join'/>
                </Button.Group>
            </Segment>
            {
                lastGame !== null &&
                <Button inverted color='violet' onClick={moveToLastGame}>
                    <Icon color='blue' name='game'/>
                    Last game
                </Button>
            }
        </div>
    )
};

export default Start;