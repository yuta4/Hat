import React, {useEffect, useState} from 'react';
import {useStoreActions, useStoreState} from 'easy-peasy';
import {Button, Divider, Dropdown, Header, Icon, Item, Label, List, Message} from 'semantic-ui-react'
import {generatingWords, teamFormation} from '../screenNames';
import ScreenHeader from './screenHeader';
import OwnerControls from './ownerControls';
import SSESubscription from '../sseSubscription';

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

    const gameProgressSubscription = new SSESubscription('/progress/events', 'gameProgress ' + gid, setTeamFormationData);

    function setTeamFormationData(eventJson) {
        setOwner(eventJson.data.owner);
        setTeams(eventJson.data.teams);
        setWatchers(eventJson.data.watchers);
        setValidation(eventJson.validation);
    }

    useEffect(() => {
        gameProgressSubscription.start();
        return () => {
            gameProgressSubscription.stop();
            client({method: 'PUT', path: '/game/changeWatcher?value=false&gameId=' + gid}).done(response => {
                console.log('TeamFormation useEffect clear changeWatcher = false');
            });
        }
    }, []);

    useEffect(() => {
        const needChange = isPlayer === isWatcher;
        console.log('TeamFormation useEffect isPlayer ' + isPlayer +
            ', isWatcher ' + isWatcher + ': ' + needChange);
        if (needChange) {
            client({
                method: 'PUT',
                path: '/game/changeWatcher?value=' + !isPlayer + '&gameId=' + gid
            }).done(response => {
                console.log('TeamFormation useEffect changeWatcher ' + !isPlayer);
            });
        }
    }, [isPlayer]);

    function closeGame() {
        if (window.confirm('Are you sure you wish to close this game?')) {
            // this.onCancel(item)
            client({method: 'PUT', path: '/game/finish?gameId=' + gid}).done(() => {
                console.log('closeGame');
                props.history.push({pathname: '/'});
            });
        }
    }

    function createTeam() {
        client({method: 'POST', path: '/team/create?gameId=' + gid}).done(() => {
            console.log('Team created');
        });
    }

    return (
        <div>
            <ScreenHeader iconName='users' iconColor='olive'
                          headerName={teamFormation} owner={owner} gid={gid}/>
            {
                isOwner &&
                <Button inverted color='violet' onClick={createTeam}>
                    <Icon name={'plus'}/>
                    Create new team
                </Button>
            }
            {
                teams.map(t =>
                    <Team key={t.id} team={t} isOwner={isOwner} login={login} possiblePlayers={watchers}/>
                )
            }
            <Watchers watchers={watchers} login={login}/>
            <Divider/>
            {
                (isWatcher && !isOwner) &&
                <Button onClick={() => {
                    moveToJoinGameOption(props.history)
                }}
                        inverted color='red'>Back to join</Button>
            }
            {
                isOwner &&
                <OwnerControls validation={validation} nextScreen={generatingWords}
                               closeGame={closeGame}/>
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
                            <Label tag color='yellow'>{watcher}</Label>
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
    const [newPlayerDropdown, setNewPlayerDropdown] = useState();

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

    function addPlayer(value) {
        setNewPlayerDropdown('');
        client({
            method: 'PUT',
            path: '/team/extend?newPlayerLogin=' + value + '&teamId=' + team.id
        }).done((response) => {
            console.log('team extended');
        }, (response) => {
            console.log('team extension error ' + response);
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
                <Dropdown
                    value={newPlayerDropdown}
                    trigger={<span><Icon name='add user'/>Add player</span>}
                          onChange={(event, data) => {
                              addPlayer(data.value)
                          }}
                          options={possiblePlayers.map(possiblePlayer => (
                              {key: possiblePlayer, value: possiblePlayer, text: possiblePlayer}))}/>

            }
        </Message>
    );
});

export default TeamFormation;