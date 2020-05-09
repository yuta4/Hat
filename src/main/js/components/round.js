import React, {useEffect, useState} from 'react';
import {useStoreActions, useStoreState} from 'easy-peasy';
import {Button, Divider, Form, Grid, Header, Icon, Label, Modal, Segment, Table} from 'semantic-ui-react'
import {rounds, summary} from '../screenUIProps';
import ScreenHeader from './screenHeader';
import OwnerControls from './ownerControls';

const client = require('../client');

const Round = (props) => {

    const [round, setRound] = useState(props.location.state.data.round);
    const roundUI = rounds.get(round);

    const [owner, setOwner] = useState(props.location.state.data.owner);
    const [allowSkipWords, setAllowSkipWords] = useState(props.location.state.data.allowSkipWords);
    const [teams, setTeams] = useState(props.location.state.data.teams);
    const [teamTurn, setTeamTurn] = useState(props.location.state.data.teamTurn);
    const [playerTurn, setPlayerTurn] = useState(props.location.state.data.playerTurn);
    const [turnStatus, setTurnStatus] = useState(props.location.state.data.turnStatus);
    const [turnGuessesCount, setTurnGuessesCount] = useState(props.location.state.data.turnGuessesCount);
    const [turnSecondsRemaining, setTurnSecondsRemaining] = useState(props.location.state.data.turnSecondsRemaining);
    const [validation, setValidation] = useState(props.location.state.validation);
    const login = useStoreState(state => state.login);
    const gid = useStoreState(state => state.gid);

    const [currentGuessingWord, setCurrentGuessingWord] = useState();
    const [turnWords, setTurnWords] = useState();
    const [wordsForApproving, setWordsForApproving] = useState();

    const addEventListener = useStoreActions(actions => actions.addEventListener);
    const removeEventListener = useStoreActions(actions => actions.removeEventListener);

    const isOwner = owner === login;

    const isActiveTurn = turnStatus === 'ACTIVE';
    const isPausedTurn = turnStatus === 'PAUSED';
    const isApprovingTurn = turnStatus === 'APPROVING';

    const isPlayerTurn = login === playerTurn;
    const turnControlsEnabled = isPlayerTurn && !isApprovingTurn;

    const isPlayerActiveTurn = isPlayerTurn && isActiveTurn;
    const needApproving = isPlayerTurn && isApprovingTurn;
    const time = Date.now();
    console.log(time + ' playerTurn = ' + playerTurn + ', turnStatus = ' + turnStatus + ' wordsForApproving = ' + wordsForApproving);

    if (needApproving) {
        if(wordsForApproving === undefined) {
            requestApprovingWords();
        }
    } else if(wordsForApproving !== undefined) {
        setWordsForApproving(undefined);
    }

    if(isPlayerActiveTurn && currentGuessingWord === undefined) {
        requestAvailableWords();
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
        setAllowSkipWords(eventJson.data.allowSkipWords);
        setTeams(eventJson.data.teams);
        console.log('setTurnStatus ' + eventJson.data.turnStatus);
        setTurnStatus(eventJson.data.turnStatus);
        setTeamTurn(eventJson.data.teamTurn);
        console.log('setPlayerTurn ' + eventJson.data.playerTurn);
        setPlayerTurn(eventJson.data.playerTurn);
        setTurnGuessesCount(eventJson.data.turnGuessesCount);
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
            } else if (isPlayerTurn) {
                client({
                    method: 'PUT',
                    path: '/turn/finish?gameId=' + gid + '&guessedWords=' + []
                }).done(() => {
                    console.log('turn finish');
                    setTurnWords(undefined);
                    setCurrentGuessingWord(undefined);
                });
            }
        }
    }, [isActiveTurn, turnSecondsRemaining]);

    function setCurrentGuessingFromParamOrRandomly(words, currentGuessing, force) {
        setTurnWords(words);
        if(currentGuessing !== undefined) {
            setCurrentGuessingWord(currentGuessing);
            return currentGuessing;
        } else if (force || currentGuessingWord === undefined) {
            const i = Math.floor(Math.random() * (words.length - 1));
            setCurrentGuessingWord(words[i]);
            return words[i];
        }
    }

    function sendCurrentGuessing(currentGuessing) {
        client({
            method: 'PUT',
            path: '/turn/current?gameId=' + gid + '&currentGuessing=' + currentGuessing
        }).done((response) => {
            console.log('turn current');
        }, (response) => {
            console.log('turn current error');
        });
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
                    const turnWords = response.entity;
                    const currentGuessing = setCurrentGuessingFromParamOrRandomly(turnWords.words,
                        turnWords.currentGuessing !== null ? turnWords.currentGuessing : undefined,
                        false);
                    if(turnWords.currentGuessing === null) {
                        sendCurrentGuessing(currentGuessing);
                    }
                }, (response) => {
                    console.log('turn/start error, paused' + isPausedTurn);
                });
                break;
            default:
                console.log('Error status');
        }
    }

    function approveTurnWords() {
        const guessedWords = wordsForApproving
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
        setWordsForApproving(undefined);
    }

    function requestAvailableWords() {
        client({
            method: 'GET',
            path: '/turn/words?gameId=' + gid
        }).done((response) => {
            console.log('turn/approve');
            const turnWords = response.entity;
            setCurrentGuessingFromParamOrRandomly(turnWords.words,
                turnWords.currentGuessing !== null ? turnWords.currentGuessing : undefined,
                false);
        }, (response) => {
            console.log('turn/approve error');
        });
    }

    function requestApprovingWords() {
        client({
            method: 'GET',
            path: '/turn/words/approving?gameId=' + gid
        }).done((response) => {
            console.log(time + ' turn words ' + JSON.stringify(response.entity));
            setWordsForApproving(response.entity);
        }, (response) => {
            console.log(time + ' turn words exception');
        })
    }

    function changeWordGuessedInApproveModal(word, checked) {
        const toChange = wordsForApproving;
        const indexOfWord = toChange.indexOf(word);
        toChange[indexOfWord].isGuessed = checked;
        setWordsForApproving([...toChange]);
    }

    function renderApprovingModal() {
        const wordsPresent = wordsForApproving !== undefined &&
            wordsForApproving.length !== 0;
        return <Modal
            open={needApproving}
            onClose={approveTurnWords}
            basic
            size='small'
        >
            <Header icon='browser' content='Check words'/>
            <Modal.Content>
                {
                    wordsPresent &&
                    <Form inverted>
                        <Form.Group grouped>
                            {
                                wordsForApproving.map(word =>
                                    <Form.Checkbox checked={word.isGuessed} label={word.word}
                                                   key={word.word}
                                                   onChange={(event, data) => {
                                                       // word.isGuessed = data.checked;
                                                       changeWordGuessedInApproveModal(word, data.checked);
                                                   }}/>)
                            }
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

    function markWord(word, isGuessed) {
        const words = turnWords.filter(w => w !== word);
        const currentGuessing = setCurrentGuessingFromParamOrRandomly(words, undefined, true);
        client({
            method: 'PUT',
            path: '/turn/mark?gameId=' + gid + '&teamId=' + teamTurn
                + '&word=' + word + '&isGuessed=' + isGuessed
                + (currentGuessing !== undefined ? '&currentGuessing=' + currentGuessing : '')
        }).done((response) => {
            console.log('turn/markWord');
        }, (response) => {
            console.log('turn/markWord error');
        });
    }

    function renderWordToGuess() {
        return <Grid>
            {
                allowSkipWords &&
                <Grid.Column width={3}>
                    <Button onClick={() => {
                        markWord(currentGuessingWord, false)
                    }} icon='close' color='red'/>
                </Grid.Column>
            }
            <Grid.Column width={8}>
                <Button basic fluid>{currentGuessingWord}</Button>
            </Grid.Column>
            <Grid.Column width={4}>
                <Button onClick={() => {
                    markWord(currentGuessingWord, true)
                }} icon labelPosition='right' color='green'>
                    <Icon name='checkmark'/>
                    {turnGuessesCount}
                </Button>
            </Grid.Column>
        </Grid>
    }

    return (
        <div>
            {
                needApproving &&
                renderApprovingModal()
            }
            <ScreenHeader ui={roundUI} owner={owner} gid={gid}/>
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
                                        <Divider hidden/>
                                        {
                                            (isPlayerActiveTurn) &&
                                            renderWordToGuess()
                                        }
                                        {
                                            !(isPlayerActiveTurn) &&
                                            <Button basic fluid>{turnGuessesCount}</Button>
                                        }
                                        <Divider hidden/>
                                        {
                                            turnControlsEnabled &&
                                            <Button as='div' fluid labelPosition='right'>
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