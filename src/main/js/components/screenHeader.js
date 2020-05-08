import Login from "./login";
import {Icon, Segment, Header} from "semantic-ui-react";
import React from "react";

const ScreenHeader = (props) => {

    const ui = props.ui;
    const owner = props.owner;
    const gid = props.gid;

    return (
        <Segment clearing secondary>
            <Login/>
            <Header as='h1' icon textAlign='center'>
                <Icon name={ui.icon} color={ui.color} circular/>
                <Header.Content>{ui.name}</Header.Content>
            </Header>
            {
                owner !== undefined &&
                <Header as='h2' floated='right'>
                    {owner}
                    <Icon name='spy'/>
                </Header>
            }
            {
                gid !== undefined &&
                <Header as='h2' floated='left'>
                    <Icon color={'blue'} name='game'/>
                    {gid}
                </Header>
            }
        </Segment>
    )
};

export default ScreenHeader;