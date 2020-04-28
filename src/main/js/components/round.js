import React, {useEffect, useState} from 'react';
import {useStoreActions, useStoreState} from 'easy-peasy';
import {Button, Divider, Dropdown, Header, Icon, Item, Label, List, Message} from 'semantic-ui-react'
import {generatingWords, secondRound, firstRound} from '../screenNames';
import ScreenHeader from './screenHeader';
import OwnerControls from './ownerControls';
import SSESubscription from '../sseSubscription';

const client = require('../client');

const Round = (props) => {

    const [owner, setOwner] = useState(props.location.state.data.owner);
    const [teams, setTeams] = useState(props.location.state.data.teams);
    const [teamTurn, setTeamTurn] = useState(props.location.state.data.teamTurn);
    const [playerTurn, setPlayerTurn] = useState(props.location.state.data.playerTurn);
    const [validation, setValidation] = useState(props.location.state.validation);
    const login = useStoreState(state => state.login);
    const gid = useStoreState(state => state.gid);

    const isOwner = owner === login;

    const gameProgressSubscription = new SSESubscription('/progress/events', 'gameProgress ' + gid,
        setTeamFormationData, props.location.pathname, props.history);

    function setTeamFormationData(eventJson) {
        setOwner(eventJson.data.owner);
        setTeams(eventJson.data.teams);
        setTeamTurn(eventJson.data.teamTurn);
        setPlayerTurn(eventJson.data.playerTurn);
        setValidation(eventJson.validation);
    }

    useEffect(() => {
        gameProgressSubscription.start();
        return () => {
            gameProgressSubscription.stop();
        }
    }, []);

    return (
        <div>
            <ScreenHeader iconName='battery one' iconColor='violet'
                          headerName={firstRound} owner={owner} gid={gid}/>
            <Divider/>
            <label>{teamTurn}</label>
            <Divider/>
            <label>{playerTurn}</label>
            {
                isOwner &&
                <OwnerControls validation={validation} nextScreen={secondRound}
                              prevScreen={generatingWords} gid={gid} history={props.history}/>
            }
        </div>
    )
};

export default Round;