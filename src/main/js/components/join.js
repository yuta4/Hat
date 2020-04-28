import React, {useEffect, useState} from 'react';
import {useStoreActions} from 'easy-peasy';
import {Button, Header, Icon, Label, Menu, Segment} from 'semantic-ui-react';
import Login from './login';
import SSESubscription from '../sseSubscription';
import {join, teamFormation} from '../screenNames';

const JoinScreen = (props) => {

    const [gamesToJoin, setGamesToJoin] = useState(props.location.state);
    const moveToGameProgressScreen = useStoreActions(actions => actions.moveToGameProgressScreen);
    const setGameId = useStoreActions(actions => actions.setGameId);

    const gamesToJoinSubscription = new SSESubscription('/game/notStarted/events', 'join',
        setGamesToJoin, undefined, props.history);

    useEffect(() => {
        console.log('JoinScreen useEffect' + {gamesToJoin});
        gamesToJoinSubscription.start();

        return () => {
            console.log('JoinScreen useEffect close');
            gamesToJoinSubscription.stop();
        }
    }, []);

    function joinGame(id) {
        setGameId(id);
        moveToGameProgressScreen(props.history);
    }

    function toMain() {
        props.history.push({pathname: '/'});
    }

    return (
        <div>
            <Segment clearing secondary>
                <Login/>
                <Header as='h1' icon textAlign='center'>
                    <Icon name={'fork'} color={'pink'} circular/>
                    <Header.Content>{join}</Header.Content>
                </Header>
            </Segment>
            <Button color='blue' onClick={() => toMain()}>To main menu</Button>
            <Menu fluid vertical>
            {gamesToJoin.map(game => (
                <Menu.Item
                    name={'' + game.gameId}
                    onClick={() => joinGame(game.gameId)}>
                    <Label color='yellow'>{game.login}</Label>
                    {game.gameId}
                </Menu.Item>
            ))}
            </Menu>
        </div>
    )
};

export default JoinScreen;