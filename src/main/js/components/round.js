import React, {useEffect, useState} from 'react';
import {useStoreActions, useStoreState} from 'easy-peasy';
import {Button, Divider, Grid, Header, Icon, Label, Segment, Table} from 'semantic-ui-react'
import {firstRound, summary} from '../screenNames';
import ScreenHeader from './screenHeader';
import OwnerControls from './ownerControls';

const client = require('../client');

const Round = (props) => {

    const [owner, setOwner] = useState(props.location.state.data.owner);
    const [teams, setTeams] = useState(props.location.state.data.teams);
    const [teamTurn, setTeamTurn] = useState(props.location.state.data.teamTurn);
    const [playerTurn, setPlayerTurn] = useState(props.location.state.data.playerTurn);
    const [turnStatus, setTurnStatus] = useState(props.location.state.data.turnStatus);
    const [turnSecondsRemaining, setTurnSecondsRemaining] = useState(props.location.state.data.turnSecondsRemaining);
    const [validation, setValidation] = useState(props.location.state.validation);
    const login = useStoreState(state => state.login);
    const gid = useStoreState(state => state.gid);

    const addEventListener = useStoreActions(actions => actions.addEventListener);
    const removeEventListener = useStoreActions(actions => actions.removeEventListener);

    const isOwner = owner === login;

    const started = turnStatus === 'ACTIVE';
    const paused = turnStatus === 'PAUSED';
    const notStarted = turnStatus === 'NOT_STARTED';

    function extractTime() {
        const minutes = turnSecondsRemaining / 60;
        const timerMinutes = minutes > 9 ? minutes : '0' + minutes;
        const seconds = turnSecondsRemaining - minutes * 60;
        const timerSeconds = seconds > 9 ? seconds : '0' + seconds;
        return timerMinutes + ' : ' + timerSeconds;
    }

    const timer = extractTime();

    function setRoundData(eventJson) {
        setOwner(eventJson.data.owner);
        setTeams(eventJson.data.teams);
        setTeamTurn(eventJson.data.teamTurn);
        setPlayerTurn(eventJson.data.playerTurn);
        setTurnStatus(eventJson.data.turnStatus);
        setTurnSecondsRemaining(eventJson.data.turnSecondsRemaining);
        setValidation(eventJson.validation);
    }

    useEffect(() => {
        addEventListener({
            url: '/progress/events', eventType: 'gameProgress ' + gid,
            path: props.location.pathname, history: props.history, handler: setRoundData
        });
        return () => {
            removeEventListener({
                url: '/progress/events', eventType: 'gameProgress ' + gid
            });
        }
    }, []);

    useEffect(() => {
        if(started) {
            if(turnSecondsRemaining > 0) {
                setTimeout(() => {
                    setTurnSecondsRemaining(turnSecondsRemaining - 1);
                }, 1000)
            } else {
                client({
                    method: 'PUT',
                    path: '/round/finish?gameId=' + gid + '&guessedWords=' + []
                }).done(() => {
                    console.log('team reduced');
                });
            }
        }
    }, [started, turnSecondsRemaining]);

    function toggleTurnStatus() {
        switch (turnStatus) {
            case 'ACTIVE':
                client({
                    method: 'PUT',
                    path: '/round/pause?gameId=' + gid
                }).done(() => {
                    console.log('team reduced');
                });
                break;
            case 'PAUSED':
                client({
                    method: 'PUT',
                    path: '/round/start?gameId=' + gid + '&wasPaused=true'
                }).done(() => {
                    console.log('team reduced');
                });
                break;
            case 'NOT_STARTED':
                client({
                    method: 'PUT',
                    path: '/round/start?gameId=' + gid + '&wasPaused=false&wordsRequested=true'
                }).done(() => {
                    console.log('team reduced');
                });
                break;
            default:
                console.log('Error status');
        }
    }

    return (
        <div>
            <ScreenHeader iconName='battery one' iconColor='violet'
                          headerName={firstRound} owner={owner} gid={gid}/>
            <Divider/>

            <Grid celled columns={2}>
                {
                    teams.map(team =>
                        <Grid.Row key={team.name} stretched>
                            {
                                (team.name === teamTurn) &&
                                <Grid.Column>
                                    <Segment basic clearing compact>
                                        <Header as='h2'>
                                            {team.name}
                                        </Header>
                                        <br/>

                                        {
                                            login === playerTurn &&
                                            <Button as='div' labelPosition='right'>
                                                <Button onClick={toggleTurnStatus} color={started ? 'red' : 'green'} icon>
                                                    <Icon name={started ? 'pause' : 'play'}/>
                                                </Button>
                                                <Label as='h3' color='blue' size='big' basic pointing='left'>
                                                    {timer}
                                                </Label>
                                            </Button>
                                        }
                                        {
                                            login !== playerTurn &&
                                            <Label as='h3' color='blue' size='big' basic>{timer}</Label>
                                        }
                                        <Table basic='very' celled collapsing>
                                            <Table.Body>
                                                {
                                                    team.players.map(player => {
                                                        if (player === playerTurn) {
                                                            return <Table.Row active key={player}>
                                                                <Table.Cell>
                                                                    {player}
                                                                </Table.Cell>
                                                            </Table.Row>
                                                        } else {
                                                            return <Table.Row key={player}>
                                                                <Table.Cell>
                                                                    {player}
                                                                </Table.Cell>
                                                            </Table.Row>
                                                        }
                                                    })
                                                }
                                            </Table.Body>
                                        </Table>
                                    </Segment>
                                </Grid.Column>
                            }
                            {
                                (team.name !== teamTurn) &&
                                <Grid.Column>
                                    <Segment basic clearing compact>
                                        <Header as='h2'>
                                            {team.name}
                                        </Header>
                                    </Segment>
                                </Grid.Column>
                            }
                            <Grid.Column as='h2' verticalAlign='middle'>
                                {team.score}
                            </Grid.Column>
                        </Grid.Row>
                    )
                }
            </Grid>

            {
                isOwner &&
                <OwnerControls validation={validation} nextScreen={summary}
                               gid={gid} history={props.history}/>
            }
        </div>
    )
};

export default Round;