import Login from "./login";
import {Icon, Segment, Header} from "semantic-ui-react";
import React from "react";

const ScreenHeader = (props) => {

    const iconName = props.iconName;
    const iconColor = props.iconColor;
    const headerName = props.headerName;
    const owner = props.owner;
    const gid = props.gid;

    return (
        <Segment clearing secondary>
            <Login/>
            <Header as='h1' icon textAlign='center'>
                <Icon name={iconName} color={iconColor} circular/>
                <Header.Content>{headerName}</Header.Content>
            </Header>
            <Header as='h2' floated='right'>
                {owner}
                <Icon name='spy'/>
            </Header>
            <Header as='h2' floated='left'>
                <Icon color={'blue'} name='game'/>
                {gid}
            </Header>
        </Segment>
    )
};

export default ScreenHeader;