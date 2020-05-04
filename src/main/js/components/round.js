import React, {useEffect, useState} from 'react';
import {useStoreActions, useStoreState} from 'easy-peasy';
import {Button, Divider, Form, Grid, Header, Icon, Label, Modal, Segment, Table} from 'semantic-ui-react'
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

    const [lastNotGuessedWord, setLastNotGuessedWord] = useState();
    const [wordsForApprovement, setWordsForApprovement] = useState();

    const addEventListener = useStoreActions(actions => actions.addEventListener);
    const removeEventListener = useStoreActions(actions => actions.removeEventListener);

    const isOwner = owner === login;

    const isActiveTurn = turnStatus === 'ACTIVE';
    const isPausedTurn = turnStatus === 'PAUSED';
    const isPlayerTurn = login === playerTurn;
    const turnControlsEnabled = isPlayerTurn && turnStatus !== 'APPROVING';

    const needApprovement = isPlayerTurn && turnStatus === 'APPROVING';
    if (needApprovement) {
        client({
            method: 'GET',
            path: '/turn/words/approvement?gameId=' + gid
        }).done((response) => {
            setWordsForApprovement(response.entity);
            console.log('turn words');
        }, (response) => {
            console.log('turn words exception');
        });
    }

    function extractTime() {
        const minutes = Math.floor(turnSecondsRemaining / 60);
        const timerMinutes = minutes > 9 ? minutes : '0' + minutes;
        const seconds = turnSecondsRemaining - (minutes * 60);
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
        if (isActiveTurn) {
            if (turnSecondsRemaining > 0) {
                setTimeout(() => {
                    setTurnSecondsRemaining(turnSecondsRemaining - 1);
                }, 1000)
            } else {
                client({
                    method: 'PUT',
                    path: '/turn/finish?gameId=' + gid + '&guessedWords=' + []
                }).done(() => {
                    console.log('team reduced');
                });
            }
        }
    }, [isActiveTurn, turnSecondsRemaining]);

    let turnWords;

    function setLastNotGuessedRandomly() {
        if (lastNotGuessedWord === undefined) {
            const i = Math.floor(Math.random() * (turnWords.size() - 1));
            setLastNotGuessedWord(turnWords[i]);
        }
    }

    function toggleTurnStatus() {
        switch (turnStatus) {
            case 'ACTIVE':
                client({
                    method: 'PUT',
                    path: '/turn/pause?gameId=' + gid
                }).done((response) => {
                    console.log('turn/pause');
                }, (response) => {
                    console.log('turn/pause error');
                });
                break;
            case 'PAUSED':
            case 'NOT_STARTED':
                client({
                    method: 'PUT',
                    path: '/turn/start?gameId=' + gid + '&wasPaused=' + isPausedTurn
                }).done((response) => {
                    console.log('turn/start, paused ' + isPausedTurn);
                    turnWords = response.entity;
                    setLastNotGuessedRandomly();
                }, (response) => {
                    console.log('turn/start error, paused' + isPausedTurn);
                });
                break;
            default:
                console.log('Error status');
        }
    }

    function approveTurnWords() {
        const guessedWords = wordsForApprovement
            .filter(word => word.isGuessed)
            .map(word => word.word);
        client({
            method: 'PUT',
            path: '/turn/approve?gameId=' + gid + '&teamId=' + teamTurn
                + '&guessedWords=' + guessedWords
        }).done((response) => {
            console.log('turn/approve');
        }, (response) => {
            console.log('turn/approve error');
        });
    }

    function renderModal() {
        const wordsPresent = wordsForApprovement !== undefined &&
            wordsForApprovement.length !== 0;
        return <Modal
            open={needApprovement}
            onClose={approveTurnWords}
            basic
            size='small'
        >
            <Header icon='browser' content='Check words'/>
            <Modal.Content>
                {
                    wordsPresent &&
                    <Form>
                        <Form.Group grouped>
                            wordsForApprovement.map(word =>
                            <Form.Checkbox checked={word.isGuessed} label={word.word}
                                           key={word.word}
                                           onChange={(event, data) => {
                                               wordsForApprovement[word.word] = data.checked;
                                           }}/>
                        </Form.Group>
                    </Form>
                }
                {
                    !wordsPresent &&
                    <h3>No words guessed</h3>
                }
            </Modal.Content>
            <Modal.Actions>
                <Button color='green' onClick={() => approveTurnWords()} inverted>
                    <Icon name='checkmark'/> Save
                </Button>
            </Modal.Actions>
        </Modal>
    }

    function renderWordToGuess() {
        return <div>
            <Button color='red'/>
            <Label>{lastNotGuessedWord}</Label>
            <Button color='green'/>
        </div>
    }

    return (
        <div>
            {
                needApprovement &&
                renderModal()
            }
            <ScreenHeader iconName='battery one' iconColor='violet'
                          headerName={firstRound} owner={owner} gid={gid}/>
            <Divider/>
            <Grid celled columns={2}>
                {
                    teams.map(team =>
                        <Grid.Row key={team.name} stretched>
                            {
                                (team.id === teamTurn) &&
                                <Grid.Column>
                                    <Segment basic clearing compact>
                                        <Header as='h2'>
                                            {team.name}
                                        </Header>
                                        <br/>
                                        {
                                            (isPlayerTurn && isActiveTurn) &&
                                            renderWordToGuess()
                                        }
                                        {
                                            turnControlsEnabled &&
                                            <Button as='div' labelPosition='right'>
                                                <Button onClick={toggleTurnStatus}
                                                        color={isActiveTurn ? 'red' : 'green'}
                                                        icon>
                                                    <Icon name={isActiveTurn ? 'pause' : 'play'}/>
                                                </Button>
                                                <Label as='h3' color='blue' size='big' basic pointing='left'>
                                                    {timer}
                                                </Label>
                                            </Button>
                                        }
                                        {
                                            !turnControlsEnabled &&
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
                                (team.id !== teamTurn) &&
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