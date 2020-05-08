import ScreenHeader from "./screenHeader";
import {summary} from "../screenUIProps";
import React, {useState} from "react";
import {useStoreActions, useStoreState} from "easy-peasy";
import {Button} from "semantic-ui-react";

const Summary = (props) => {

    const [owner, setOwner] = useState(props.location.state.data.owner);
    const gid = useStoreState(state => state.gid);
    const moveToJoinGameOption = useStoreActions(actions => actions.moveToJoinGameOption);

    return (
        <div>
            <ScreenHeader ui={summary} owner={owner} gid={gid}/>
            <Button onClick={() => {
                moveToJoinGameOption(props.history)
            }}
                    inverted color='red'>Back to join</Button>

        </div>
    )
};

export default Summary;