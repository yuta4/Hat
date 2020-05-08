import React, {useEffect, useState} from 'react';
import {useStoreActions} from 'easy-peasy';
import {Button, Label, Menu} from 'semantic-ui-react';
import {join} from '../screenUIProps';
import ScreenHeader from "./screenHeader";

const JoinScreen = (props) => {

    const [gamesToJoin, setGamesToJoin] = useState(props.location.state);
    const moveToGameProgressScreen = useStoreActions(actions => actions.moveToGameProgressScreen);
    const setGameId = useStoreActions(actions => actions.setGameId);
    const addEventListener = useStoreActions(actions => actions.addEventListener);
    const removeEventListener = useStoreActions(actions => actions.removeEventListener);

    useEffect(() => {
        addEventListener({
            url: '/game/notStarted/events', eventType: 'join',
            path: undefined, history: props.history, handler: setGamesToJoin
        });
        return () => {
            removeEventListener({
                url: '/game/notStarted/events', eventType: 'join'
            });
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
            <ScreenHeader ui={join}/>
            <Button color='blue' onClick={() => toMain()}>To main menu</Button>
            <Menu fluid vertical>
            {gamesToJoin.map(game => (
                <Menu.Item
                    key={game.gameId}
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