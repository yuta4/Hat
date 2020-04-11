import React, {useEffect} from 'react';
import {useStoreState} from "easy-peasy";
import {Label} from 'semantic-ui-react'
import Select from 'react-select'
import Login from "./login";

const TeamFormation = (props) => {

    function handleNewPlayer(event) {
        const selected = event.target.value;
    }


    const players = useStoreState(state => state.game_players);
    const owner = useStoreState(state => state.owner);

    console.log('TeamFormation render id ' + players + ',' + props.match.params.gid);
    return (
        <div>
            <Login/>
            <h1>TeamFormation {props.match.params.gid}</h1>
            <Team playersAvailable={players} handleNewPlayer={handleNewPlayer}/>
            <p/>
            <Watchers/>
        </div>
    )
};

const Watchers = () => {
    const watchers = useStoreState(state => state.game_watchers);
    const owner = useStoreState(state => state.owner);

    return (
        <div>
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