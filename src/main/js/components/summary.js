import ScreenHeader from "./screenHeader";
import {summary} from "../screenNames";
import React, {useEffect, useState} from "react";
import {useStoreActions, useStoreState} from "easy-peasy";
import {Button} from "semantic-ui-react";

const client = require('../client');

const Summary = (props) => {

    const [owner, setOwner] = useState(props.location.state.data.owner);
    const gid = useStoreState(state => state.gid);
    const moveToJoinGameOption = useStoreActions(actions => actions.moveToJoinGameOption);

    return (
        <div>
            <ScreenHeader iconName='table' iconColor='red'
                          headerName={summary} owner={owner} gid={gid}/>
            <Button onClick={() => {
                moveToJoinGameOption(props.history)
            }}
                    inverted color='red'>Back to join</Button>

        </div>
    )
};

export default Summary;