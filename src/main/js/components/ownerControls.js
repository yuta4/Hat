import {Button, Label, List, Segment} from "semantic-ui-react";
import React from "react";

const OwnerControls = (props) => {

    const validation = props.validation;
    const isValidationPassed = validation.trim() === '';
    const prevScreen = props.prevScreen;
    const nextScreen = props.nextScreen;
    const closeGame = props.closeGame;

    return (
        <List>
            <List.Item>
                <Segment basic>
                    {
                        !isValidationPassed &&
                        <Label basic color='red' ribbon={'right'} >{validation}</Label>
                    }
                    <br />
                    <Button disabled={!isValidationPassed} onClick={nextScreen} color={"green"} floated={'right'}>Next</Button>
                    <Button onClick={prevScreen} inverted color={"green"} >Back</Button>
                </Segment>
            </List.Item>
            <List.Item>
                <Button color='red' onClick={closeGame}>Close game</Button>
            </List.Item>
        </List>
    );
};

export default OwnerControls;