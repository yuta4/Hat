import React from 'react';
import {useStoreActions} from "easy-peasy";
import Login from "./login";
import {Header, Icon, Segment, Button} from "semantic-ui-react";
import logo from '../images/hat.png';

// const src = require('../images/hat.png');

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
        <Segment clearing secondary>
            <Login/>
            <Header image={logo}
                    as='h1' textAlign='center'>
                <Header.Content>Hat online</Header.Content>
            </Header>
            <Button.Group attached='bottom'>
                <Button onClick={createGame} color='green' icon='file outline' content='Create'/>
                <Button.Or />
                <Button onClick={() => moveToJoinGameOption(props.history)} color='pink' icon='fork'
                        content='Join'/>
            </Button.Group>
        </Segment>
    )
};

export default NewGame;