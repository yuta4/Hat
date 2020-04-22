import React, {useEffect, useState} from 'react';
import {useStoreActions, useStoreState} from 'easy-peasy';
import {Button, Divider, Header, Icon, Item, Label, List, Message, Segment} from 'semantic-ui-react'
import Select from 'react-select'
import Login from './login';

const client = require('../client');

const TeamFormation = (props) => {

    const [owner, setOwner] = useState(props.location.state.data.owner);
    const [teams, setTeams] = useState(props.location.state.data.teams);
    const [watchers, setWatchers] = useState(props.location.state.data.watchers);
    const [validation, setValidation] = useState(props.location.state.validation);
    const login = useStoreState(state => state.login);
    const gid = useStoreState(state => state.gid);
    const moveToJoinGameOption = useStoreActions(actions => actions.moveToJoinGameOption);
    const isValidationPassed = validation.trim() === '';

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
            this.source = new EventSource('/progress/events');

            this.source.addEventListener('gameProgress ' + gid, function (event) {
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
        console.log('TeamFormation useEffect');
        client({method: 'PUT', path: '/game/changeWatcher?value=' + !isPlayer + '&gameId=' + gid}).done(response => {
            console.log('TeamFormation useEffect changeWatcher ' + !isPlayer);
        });
    }, [isPlayer]);

    function closeGame() {
        client({method: 'PUT', path: '/game/finish?gameId=' + gid}).done(() => {
            console.log('closeGame');
            props.history.push({pathname: '/'});
        });
    }

    function createTeam() {
        client({method: 'POST', path: '/team/create?gameId=' + gid}).done(() => {
            console.log('Team created');
        });
    }

    function nextScreen() {

    }

    return (
        <div>
            <Segment clearing secondary>
                <Login/>
                <Header as='h1' icon textAlign='center'>
                    <Icon name='users' color={'olive'} circular/>
                    <Header.Content>TeamFormation</Header.Content>
                </Header>
                <Header as='h2' floated='right'>
                    {owner}
                    <Icon name='spy'/>
                </Header>
                <Header as='h2' floated='left'>
                    <Icon color={'blue'} name='game'/>
                    {gid}
                </Header>
            </Segment>
            {
                (isWatcher && !isOwner) &&
                <Button onClick={() => {
                    console.log('TeamFormation isWatcher = false, moveToJoinGameOption');
                    client({method: 'PUT', path: '/game/changeWatcher?value=false&gameId=' + gid}).done(response => {
                        console.log('TeamFormation useEffect clear changeWatcher = false');
                        moveToJoinGameOption(props.history)
                    });

                }}>Back to join</Button>
            }
            {
                isOwner &&
                <Button onClick={createTeam}>Create new team</Button>
            }
            {
                teams.map(t =>
                    <Team key={t.id} team={t} isOwner={isOwner} login={login} possiblePlayers={watchers}/>
                )
            }
            <Watchers watchers={watchers} login={login}/>
            <Divider/>
            {
                isOwner &&
                <List>
                    <List.Item>
                        <Segment basic>
                            {
                                !isValidationPassed &&
                                <Label basic color='red' pointing='below' attached={'top right'}>{validation}</Label>
                            }
                            <Button onClick={nextScreen} floated={'right'}>Next</Button>
                        </Segment>
                    </List.Item>
                    <List.Item>
                        <Button color='red' onClick={closeGame}>Close game</Button>
                    </List.Item>
                </List>
            }
        </div>
    )
};

const Watchers = (props) => {
    const watchers = props.watchers;
    const login = props.login;

    return (
        <List animated verticalAlign='middle'>
            {watchers.map(watcher => (
                <List.Item key={watcher}>
                    <List.Content>
                        {
                            watcher === login &&
                            <Label tag color='teal'>{watcher}</Label>
                        }
                        {
                            watcher !== login &&
                            <Label tag color='green'>{watcher}</Label>
                        }
                    </List.Content>
                </List.Item>
            ))}
        </List>
    )
};

const Team = ((props) => {

    const team = props.team;
    const isOwner = props.isOwner;
    const login = props.login;
    const possiblePlayers = props.possiblePlayers;

    function deleteTeam() {
        client({method: 'DELETE', path: '/team/delete?teamId=' + team.id}).done(() => {
            console.log('Team removed');
        }, response => {
            console.log('Team removed failed ' + response);
        });
    }

    function leaveTeam(playerName) {
        client({
            method: 'PUT',
            path: '/team/reduce?playerLogin=' + playerName + '&teamId=' + team.id + '&moveToWatchers=true'
        }).done(() => {
            console.log('team reduced');
        });
    }

    return (
        <Message>
            <Header block as='h2'>
                <Item>
                    <Icon color={'violet'} name='address card'/>
                    Team {team.name}
                    {
                        isOwner &&
                        <Button color={'red'} floated={'right'} icon={'remove circle'} onClick={deleteTeam}/>
                    }
                </Item>
            </Header>
            <List divided verticalAlign='middle'>
                {
                    team.players.map(playerName =>
                        <List.Item key={playerName} as={'h3'}>
                            {
                                (isOwner || playerName === login) &&
                                <List.Content floated='right'>
                                    <Button color={'red'} icon={'user delete'} onClick={() => leaveTeam(playerName)}/>
                                </List.Content>
                            }
                            <List.Content>
                                {playerName}
                            </List.Content>

                        </List.Item>
                    )
                }
            </List>
            {
                isOwner &&
                <NewPlayer possiblePlayers={possiblePlayers} teamId={team.id}/>
            }
        </Message>
    );
});

const NewPlayer = ((props) => {
    const possiblePlayers = props.possiblePlayers;
    const teamId = props.teamId;

    function addPlayer(value) {
        client({method: 'PUT', path: '/team/extend?newPlayerLogin=' + value + '&teamId=' + teamId}).done((response) => {
            console.log('team extended');
        }, (response) => {
            console.log('team extension error ' + response);
        });
    }

    return <Select onChange={event => {
        addPlayer(event.value)
    }
    } options={possiblePlayers.map(possiblePlayer => ({value: possiblePlayer, label: possiblePlayer}))}/>
});

export default TeamFormation;