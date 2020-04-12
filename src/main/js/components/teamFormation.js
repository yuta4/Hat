import React, {useEffect} from 'react';
import {useStoreState} from "easy-peasy";
import {Label} from 'semantic-ui-react'
import Select from 'react-select'
import Login from "./login";
import Button from "semantic-ui-react/dist/commonjs/elements/Button";
const client = require('../client');

const TeamFormation = (props) => {

    function handleNewPlayer(event) {
        const selected = event.target.value;
    }

    const players = useStoreState(state => state.game_players);
    const owner = useStoreState(state => state.owner);
    const gid = useStoreState(state => state.gid);
    const login = useStoreState(state => state.login);
    const game_watchers = useStoreState(state => state.game_watchers);
    const isWatcher = game_watchers.includes(login);
    const isPlayer = players.includes(login);
    const isOwner = owner === login;

    useEffect(() => {
        console.log("TeamFormation useEffect");
       return () => {
           if(isWatcher) {
               console.log("leaveTeamFormation");
               client({method: 'PUT', path: '/game/unwatch?gameId=' + gid}).done(response => {
                   console.log("unwatched");
               });
           }
       };
    });

    console.log('TeamFormation render id ' + players + ',' + props.match.params.gid);

    function toJoinScreen() {
        props.history.push({pathname: '/join'});
    }

    function closeGame() {
        client({method: 'PUT', path: '/game/finish?gameId=' + gid}).done(() => {
            console.log("closeGame");
            props.history.push({pathname: '/'});
        });
    }

    return (
        <div>
            <Login/>
            <h1>TeamFormation {props.match.params.gid}</h1>
            <Team playersAvailable={players} handleNewPlayer={handleNewPlayer}/>
            {isWatcher &&
                <Button onClick={() => toJoinScreen()}>Back to join</Button>
            }
            {isOwner &&
            <Button onClick={() => closeGame()}>Close game</Button>
            }
            <Watchers/>
        </div>
    )
};

const Watchers = () => {
    const watchers = useStoreState(state => state.game_watchers);
    const owner = useStoreState(state => state.owner);

    return (
        <div>
            <p/>
            <Label color='red' key={owner} horizontal>{owner}</Label>
            {watchers.map(watcher => (
                <Label color='green' key={watcher} horizontal>{watcher}</Label>
            ))}
        </div>
    )
};

const Team = ((props) => {

    const owner = useStoreState(state => state.owner);

    return (
        <NewPlayer playersAvailable={props.playersAvailable} handleNewPlayer={props.handleNewPlayer}/>
    );
});

const Player = (() => {


});

const NewPlayer = ((props) => {

    const watchers = [...useStoreState(state => state.game_watchers),useStoreState(state => state.owner)];
    return <Select options={watchers.map(watcher => ({value: watcher, label: watcher}))}/>
});

export default TeamFormation;