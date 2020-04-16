import React, {useEffect, useState} from 'react';
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

    const [owner, setOwner] = useState(props.location.state.owner);
    const [teams, setTeams] = useState(props.location.state.teams);
    const [watchers, setWatchers] = useState(props.location.state.watchers);

    const login = useStoreState(state => state.login);
    const gid = useStoreState(state => state.gid);

    function checkPlayer(player) {
        return teams.flatMap(team => team.players).includes(player);
    }

    // const isPlayer = checkPlayer(login);
    const isWatcher = watchers.includes(login);
    const isOwner = owner === login;

    const possiblePlayers = checkPlayer(owner) ? watchers : [...watchers, owner];

    useEffect(() => {
        console.log("TeamFormation useEffect");
        return () => {
            if (isWatcher) {
                console.log("leaveTeamFormation");
                client({method: 'PUT', path: '/game/unwatch?gameId=' + gid}).done(response => {
                    console.log("unwatched");
                });
            }
        };
    });

    function toJoinScreen() {
        props.history.push({pathname: '/join'});
    }

    function closeGame() {
        client({method: 'PUT', path: '/game/finish?gameId=' + gid}).done(() => {
            console.log("closeGame");
            props.history.push({pathname: '/'});
        });
    }

    function createTeam() {
        client({method: 'POST', path: '/team/create?gameId=' + gid}).done(() => {
            console.log("Team created");
        });
    }

    return (
        <div>
            <Login/>
            <h1>TeamFormation {gid}</h1>
            {
                teams.map(t =>
                    <Team key={t.id} team={t} isOwner={isOwner} login={login} possiblePlayers={possiblePlayers}/>
                )
            }
            {isWatcher &&
            <Button onClick={() => toJoinScreen()}>Back to join</Button>
            }
            {isOwner &&
            <Button onClick={() => closeGame()}>Close game</Button> &&
            <Button onClick={createTeam}>Create new team</Button>
            }
            <Watchers owner={owner} watchers={watchers}/>
        </div>
    )
};

const Watchers = (props) => {
    const watchers = props.watchers;
    const owner = props.owner;

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

    const team = props.team;
    const isOwner = props.isOwner;
    const login = props.login;
    const possiblePlayers = props.possiblePlayers;

    return (
        <div>
            <h2>Team {team.name}</h2>
            {
                team.players.map(playerName =>
                    <Player key={playerName} name={playerName} login={login}/>
                )
            }
            {
                isOwner &&
                <NewPlayer possiblePlayers={possiblePlayers} teamId={team.id}/>
            }
        </div>
    );
});

const Player = ((props) => {
    function leaveTeam() {
        client({method: 'PUT', path: '/team/reduce?playerLogin=&teamId=' + gid}).done(() => {
            console.log("closeGame");
            props.history.push({pathname: '/'});
        });
    }

    const login = props.login;
    const name = props.name;

    return (
        <div>
            <h3>{name}</h3>
            {login === name &&
            <Button onClick={() => leaveTeam()}>Leave team</Button>
            };
        </div>
    )

});

const NewPlayer = ((props) => {
    const possiblePlayers = props.possiblePlayers;
    const teamId = props.teamId;

    function addPlayer (value) {
        client({method: 'PUT', path: '/team/extend?newPlayerLogin=' + value + '&teamId=' + teamId}).done(() => {
            console.log("team extended");
        });
    }

    return <Select onChange={event => {
        addPlayer(event.value)}
    } options={possiblePlayers.map(possiblePlayer => ({value: possiblePlayer, label: possiblePlayer}))}/>
});

export default TeamFormation;