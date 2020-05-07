import ScreenHeader from "./screenHeader";
import {firstRound, generatingWords, teamFormation} from "../screenNames";
import React, {useEffect, useState} from "react";
import OwnerControls from "./ownerControls";
import {useStoreActions, useStoreState} from "easy-peasy";
import {Form, Message} from "semantic-ui-react";

const client = require('../client');

const GenerateWords = (props) => {

    const possibleWordsPerPlayer = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 22, 24, 26, 28, 30, 35, 40, 45, 50, 55, 60];
    const possibleWordsCountOptions = possibleWordsPerPlayer
        .map(count => ({key: count, text: '' + count, value: count}));

    const [validation, setValidation] = useState(props.location.state.validation);
    const [owner, setOwner] = useState(props.location.state.data.owner);
    const [languages, setLanguages] = useState(props.location.state.data.wordsLanguages);
    const [levels, setLevels] = useState(props.location.state.data.wordsLevels);
    const initialWordsPerPlayer = props.location.state.data.wordsPerPlayer;
    const [wordsPerPlayer, setWordsPerPlayer] = useState(initialWordsPerPlayer === null || initialWordsPerPlayer === undefined ? '' : initialWordsPerPlayer);
    const [allowSkipWords, setAllowSkipWords] = useState(props.location.state.data.allowSkipWords);
    const [gameWords, setGameWords] = useState(props.location.state.data.gameWords);
    const login = useStoreState(state => state.login);
    const gid = useStoreState(state => state.gid);
    const isOwner = owner === login;
    const addEventListener = useStoreActions(actions => actions.addEventListener);
    const removeEventListener = useStoreActions(actions => actions.removeEventListener);

    function setGenerateWordsData(eventJson) {
        setOwner(eventJson.data.owner);
        setLanguages(eventJson.data.wordsLanguages);
        setLevels(eventJson.data.wordsLevels);
        setGameWords(eventJson.data.gameWords);
        setWordsPerPlayer(eventJson.data.wordsPerPlayer);
        setAllowSkipWords(eventJson.data.allowSkipWords);
        setValidation(eventJson.validation);
    }

    useEffect(() => {
        addEventListener({
            url: '/progress/events', eventType: 'gameProgress ' + gid,
            path: props.location.pathname, history: props.history, handler: setGenerateWordsData
        });
        return () => {
            removeEventListener({
                url: '/progress/events', eventType: 'gameProgress ' + gid
            });
        }
    }, []);

    function levelChange(level, checked) {
        console.log('GenerateWord levelChange ' + level + ' ' + checked);
        client({
            method: 'PUT',
            path: '/words/level?gameId=' + gid + '&level=' + level + '&value=' + checked
        }).done(response => {
            console.log('GenerateWord levelChange success');
        });
    }

    function languageChange(language, checked) {
        console.log('GenerateWord languageChange ' + language + ' ' + checked);
        client({
            method: 'PUT',
            path: '/words/language?gameId=' + gid + '&language=' + language + '&value=' + checked
        }).done(response => {
            console.log('GenerateWord languageChange success');
        });
    }

    function onWordsPerPlayer(event, data) {
        console.log('GenerateWord onWordsPerPlayer ' + data.value);
        client({
            method: 'PUT',
            path: '/words/amount?gameId=' + gid + '&value=' + data.value
        }).done(response => {
            console.log('GenerateWord amount success');
        });
    }

    function onSkipWordsChange(event, data) {
        console.log('GenerateWord onSkipWordsChange ' + data.checked);
        client({
            method: 'PUT',
            path: '/words/skip?gameId=' + gid + '&value=' + data.checked
        }).done(response => {
            console.log('GenerateWord amount success');
        });
    }

    return (
        <div>
            <ScreenHeader iconName='unordered list' iconColor='orange'
                          headerName={generatingWords} owner={owner} gid={gid}/>

            <Form>
                <Form.Group grouped>
                    <label>Words language</label>
                    {
                        languages.map(language =>
                            <Form.Checkbox disabled={!isOwner} checked={language.value} label={language.name}
                                           key={language.name}
                                           onChange={(event, data) => {
                                               languageChange(language.name, data.checked)
                                           }}/>
                        )
                    }
                </Form.Group>
                <Form.Group grouped>
                    <label>Words level</label>
                    {
                        levels.map(level =>
                            <Form.Checkbox disabled={!isOwner} checked={level.value} label={level.name} key={level.name}
                                           onChange={(event, data) => {
                                               levelChange(level.name, data.checked)
                                           }}/>
                        )
                    }
                </Form.Group>
                <Form.Group grouped>
                    <label>Rules</label>
                    <Form.Checkbox disabled={!isOwner} checked={allowSkipWords} label='Allow skip words'
                                   onChange={onSkipWordsChange}/>
                </Form.Group>
                <Form.Group>
                    {
                        isOwner &&
                        <Form.Select options={possibleWordsCountOptions} value={wordsPerPlayer}
                                     onChange={onWordsPerPlayer}
                                     label='Words per player' search/>
                    }
                    {
                        !isOwner &&
                        <Form.Input value={wordsPerPlayer} label='Words per player' readOnly/>
                    }
                </Form.Group>
                <Message
                    visible
                    success={wordsPerPlayer > 0}
                    header='Total words'
                    content={gameWords}
                />
            </Form>
            {
                isOwner &&
                <OwnerControls validation={validation} nextScreen={firstRound}
                               prevScreen={teamFormation} gid={gid}  history={props.history}/>
            }
        </div>
    )
};

export default GenerateWords;