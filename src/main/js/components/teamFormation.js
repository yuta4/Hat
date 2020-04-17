import React, {useEffect, useState} from 'react';
import {useStoreActions, useStoreState} from "easy-peasy";
import {Button, Label} from 'semantic-ui-react'
import Select from 'react-select'
import Login from "./login";

const client = require('../client');

const TeamFormation = (props) => {

    const [owner, setOwner] = useState(props.location.state.data.owner);
    const [teams, setTeams] = useState(props.location.state.data.teams);
    const [watchers, setWatchers] = useState(props.location.state.data.watchers);
    const [validation, setValidation] = useState(props.location.state.validation);
    const login = useStoreState(state => state.login);
    const gid = useStoreState(state => state.gid);
    const moveToJoinGameOption = useStoreActions(actions => actions.moveToJoinGameOption);
    const isValidationPassed = validation.trim() === "";

    const gameProgressSubscription = new gameProgressSubscriptionEvent();

    function gameProgressSubscriptionEvent() {

        this.source = null;

        this.start = function () {
            console.log('gameProgressSubscriptionEvent before EventSource');
            this.source = new EventSource("/progress/events");

            this.source.onmessage = function (event) {
                console.log('gameProgressSubscriptionEvent onmessage for debug');
            };

            this.source.addEventListener("gameProgress " + gid, function (event) {
                console.log('Got update gameProgressSubscriptionEvent ' + event);
                let eventJson = JSON.parse(event.data);
                setOwner(eventJson.data.owner);
                setTeams(eventJson.data.teams);
                setWatchers(eventJson.data.watchers);
                setValidation(eventJson.validation);
            });

            this.source.onerror = function (event) {
                // this.close();
                console.log('Got update error ' + event);
            };

        };

        this.stop = function () {
            this.source.close();
        };

    }

    // const isPlayer = checkPlayer(login);
    const isWatcher = watchers.includes(login);
    const isOwner = owner === login;

    useEffect(() => {
        gameProgressSubscription.start();

        return () => {
            gameProgressSubscription.stop();
        }
    });

    useEffect(() => {
        console.log("TeamFormation useEffect");
        return () => {
            if (isWatcher && !isOwner) {
                console.log("leaveTeamFormation");
                client({method: 'PUT', path: '/game/unwatch?gameId=' + gid}).done(response => {
                    console.log("unwatched");
                });
            }
        };
    });

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

    function nextScreen() {

    }

    return (
        <div>
            <Login/>
            <h1>TeamFormation: game {gid}, owner {owner}</h1>
            {
                (isWatcher && !isOwner) &&
                <Button onClick={() => moveToJoinGameOption(props.history)}>Back to join</Button>
                // &&
                // <p/>
            }
            {
                isOwner &&
                <Button onClick={closeGame}>Close game</Button>
                // &&
                // <p/>
            }
            {
                isOwner &&
                <Button onClick={createTeam}>Create new team</Button>
                // &&
                // <p/>
            }
            {
                teams.map(t =>
                    <Team key={t.id} team={t} isOwner={isOwner} login={login} possiblePlayers={watchers}/>
                )
            }
            <Watchers owner={owner} watchers={watchers}/>
            <p/>
            {
                isOwner &&
                <Button disabled={!isValidationPassed} onClick={nextScreen}>Next</Button>
            }
            {
                (isOwner && !isValidationPassed) &&
                <Label>{validation}</Label>
            }
        </div>
    )
};

const Watchers = (props) => {
    const watchers = props.watchers;
    const owner = props.owner;

    return (
        <div>
            <p/>
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

    function deleteTeam() {
        client({method: 'DELETE', path: '/team/delete?teamId=' + team.id}).done(() => {
            console.log("Team removed");
        }, response => {
            console.log("Team removed failed " + response);
        });
    }

    return (
        <div>
            <h2>Team {team.name}</h2>
            {
                isOwner &&
                <Button onClick={deleteTeam}>X</Button>
            }
            {
                team.players.map(playerName =>
                    <Player key={playerName} isOwner={isOwner} name={playerName} login={login}
                            teamId={team.id}/>
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

    const login = props.login;
    const name = props.name;
    const teamId = props.teamId;
    const isOwner = props.isOwner;
    const isPlayer = name === login;

    function leaveTeam() {
        client({
            method: 'PUT',
            path: '/team/reduce?playerLogin=' + login + '&teamId=' + teamId + '&moveToWatchers=true'
        }).done(() => {
            console.log("team reduced");
            // props.history.push({pathname: '/'});
        });

    }

    return (
        <div>
            <h3>{name}</h3>
            {
                isPlayer &&
                <Button onClick={leaveTeam}>Leave team</Button>
            }
            {
                (!isPlayer && isOwner) &&
                <Button onClick={leaveTeam}>X</Button>
            }
        </div>
    )

});

const NewPlayer = ((props) => {
    const possiblePlayers = props.possiblePlayers;
    const teamId = props.teamId;

    function addPlayer(value) {
        client({method: 'PUT', path: '/team/extend?newPlayerLogin=' + value + '&teamId=' + teamId}).done((response) => {
            console.log("team extended");
        }, (response) => {
            console.log("team extension error " + response);
        });
    }

    return <Select onChange={event => {
        addPlayer(event.value)
    }
    } options={possiblePlayers.map(possiblePlayer => ({value: possiblePlayer, label: possiblePlayer}))}/>
});

export default TeamFormation;