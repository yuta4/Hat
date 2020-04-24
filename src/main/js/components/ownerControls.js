import {Button, Label, List, Segment} from "semantic-ui-react";
import React from "react";

const client = require('../client');

const OwnerControls = (props) => {

    const validation = props.validation;
    const isValidationPassed = validation.trim() === '';
    const prevScreen = props.prevScreen;
    const nextScreen = props.nextScreen;
    const closeGame = props.closeGame;

    function moveProgress(progress) {
        client({
            method: 'PUT', path: '/progress/move?gameId=' + gid +
                '&progressToMoveTo=' + progress
        }).done(response => {
            console.log('progress move' + response.entity + gid);
            history.push({pathname: response.entity.path, state: response.entity})
        }, response => {
            console.log('moveToGameProgressScreen error ' + response.status);
        });
    }

    return (
        <List>
            <List.Item>
                <Segment basic>
                    {
                        (nextScreen !== undefined && !isValidationPassed) &&
                        <Label basic color='red' ribbon={'right'}>{validation}</Label>
                    }
                    <br/>
                    {
                        nextScreen !== undefined &&
                        <Button disabled={!isValidationPassed} onClick={() => moveProgress(nextScreen)} color={"green"}
                                floated={'right'}>{nextScreen}</Button>
                    }
                    {
                        prevScreen !== undefined &&
                        <Button onClick={() => moveProgress(prevScreen)} inverted color={"green"}>{prevScreen}</Button>
                    }
                </Segment>
            </List.Item>
            <List.Item>
                <Button color='red' onClick={closeGame}>Close game</Button>
            </List.Item>
        </List>
    );
};

export default OwnerControls;