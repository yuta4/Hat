import React, {useEffect, useState} from 'react';
import {useStoreActions, useStoreState} from "easy-peasy";
import {Label} from 'semantic-ui-react'
import {Badge, Button, Col, Container, Row} from 'react-bootstrap'
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

    const isPlayer = teams.flatMap(team => team.players).includes(login);
    const isWatcher = watchers.includes(login);
    const isOwner = owner === login;

    console.log('TeamFormation init isPlayer ' + isPlayer + ' isWatcher ' + isWatcher + ' isOwner ' + isOwner);
    console.log('TeamFormation init owner ' + owner + ' teams ' + JSON.stringify(teams) + ' watchers ' + watchers);
    console.log('TeamFormation init validation ' + validation + ' login ' + login + ' gid ' + gid);

    const gameProgressSubscription = new gameProgressSubscriptionEvent();

    function setTeamFormationData(eventJson) {
        setOwner(eventJson.data.owner);
        setTeams(eventJson.data.teams);
        setWatchers(eventJson.data.watchers);
        setValidation(eventJson.validation);
    }

    function gameProgressSubscriptionEvent() {

        this.source = null;

        this.start = function () {
            console.log('gameProgressSubscriptionEvent start');
            this.source = new EventSource("/progress/events");

            // this.source.onmessage = function (event) {
            //     // console.log('gameProgressSubscriptionEvent onmessage for debug');
            // };

            this.source.addEventListener("gameProgress " + gid, function (event) {
                let eventJson = JSON.parse(event.data);
                console.log('Got update ' + event.lastEventId + ' gameProgressSubscriptionEvent ' + JSON.stringify(eventJson));
                setTeamFormationData(eventJson);
            });

            this.source.onerror = function (event) {
                // this.close();
                console.log('Got update error ' + event);
            };

        };

        this.stop = function () {
            console.log('gameProgressSubscriptionEvent stop');
            this.source.close();
        };

    }

    useEffect(() => {
        gameProgressSubscription.start();

        return () => {
            gameProgressSubscription.stop();
        }
    });

    useEffect(() => {
        console.log("TeamFormation useEffect");
        client({method: 'PUT', path: '/game/changeWatcher?value=' + !isPlayer + '&gameId=' + gid}).done(response => {
            console.log("TeamFormation useEffect changeWatcher " + !isPlayer);
        });
        // return () => {
        //     if (isWatcher) {
        //         console.log("leaveTeamFormation");
        //         client({method: 'PUT', path: '/game/changeWatcher?value=false&gameId=' + gid}).done(response => {
        //             console.log("useEffect clear");
        //         });
        //     }
        // };
    }, [isPlayer]);

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
        <Container>
            <Login/>
            <h1>TeamFormation: game {gid}, owner {owner}</h1>
            {
                (isWatcher && !isOwner) &&
                <Button onClick={() => {
                    console.log("TeamFormation clear effect isWatcher " + isWatcher);
                    client({method: 'PUT', path: '/game/changeWatcher?value=false&gameId=' + gid}).done(response => {
                        console.log("TeamFormation useEffect clear changeWatcher = false");
                        moveToJoinGameOption(props.history)
                    });

                }}>Back to join</Button>
                // &&
                // <p/>
            }
            {
                isOwner &&
                <Button variant="danger" onClick={closeGame}>Close game</Button>
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
        </Container>
    )
};

const Watchers = (props) => {
    const watchers = props.watchers;

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
                <Button variant="outline-danger" onClick={deleteTeam}>X</Button>
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
        <Container>
            <Row>
                <Col><Badge variant="success">{name}</Badge></Col>
                {
                    isPlayer &&
                    <Col><Button variant="outline-danger" onClick={leaveTeam}>Leave team</Button></Col>
                }
                {
                    (!isPlayer && isOwner) &&
                    <Col><Button variant="outline-danger" onClick={leaveTeam}>X</Button></Col>
                }
            </Row>
        </Container>
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