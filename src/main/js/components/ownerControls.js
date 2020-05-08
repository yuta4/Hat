import {Button, Icon, Label, List, Segment} from "semantic-ui-react";
import React from "react";

const client = require('../client');

const OwnerControls = (props) => {

    const validation = props.validation;
    const isValidationPassed = validation.trim() === '';
    const prevScreen = props.prevScreen;
    const nextScreen = props.nextScreen;
    const gid = props.gid;
    const history = props.history;

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

    function closeGame() {
        if (window.confirm('Are you sure you wish to close this game?')) {
            client({method: 'PUT', path: '/game/finish?gameId=' + gid}).done(() => {
                console.log('closeGame');
                // history.push({pathname: '/'});
            });
        }
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
                        <Button disabled={!isValidationPassed} onClick={() => moveProgress(nextScreen.name)}
                                icon labelPosition='right' color={nextScreen.color} floated={'right'}>
                            <Icon name={nextScreen.icon}/>
                            {nextScreen.name}
                        </Button>
                    }
                    {
                        prevScreen !== undefined &&
                        <Button onClick={() => moveProgress(prevScreen.name)}
                                icon labelPosition='left' color={prevScreen.color}>
                            {prevScreen.name}
                            <Icon name={prevScreen.icon}/>
                        </Button>
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