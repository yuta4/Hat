import ScreenHeader from "./screenHeader";
import {summary} from "../screenUIProps";
import React, {useState} from "react";
import {useStoreActions, useStoreState} from "easy-peasy";
import {Button, Divider, Grid, Header, Segment, Table} from "semantic-ui-react";

const Summary = (props) => {

    const [owner, setOwner] = useState(props.location.state.data.owner);
    const [teams, setTeams] = useState(props.location.state.data.teams);
    const gid = useStoreState(state => state.gid);
    const moveToJoinGameOption = useStoreActions(actions => actions.moveToJoinGameOption);

    const winnerScore = Math.max.apply(Math,teams.map(team => team.score));

    return (
        <div>
            <ScreenHeader ui={summary} owner={owner} gid={gid}/>

            <Divider/>
            <Grid celled columns={2}>
                {
                    teams.map(team =>
                        <Grid.Row key={team.name} stretched>
                            <Grid.Column>
                                <Segment basic={team.score !== winnerScore}
                                         inverted={team.score === winnerScore}
                                         color={team.score === winnerScore ? 'yellow' : null}
                                         clearing compact>
                                    <Header as='h2'>
                                        {team.name}
                                    </Header>
                                    <Table basic='very' celled collapsing>
                                        <Table.Body>
                                            {
                                                team.players.map(player => {
                                                    return <Table.Row key={player}>
                                                        <Table.Cell>
                                                            {player}
                                                        </Table.Cell>
                                                    </Table.Row>
                                                })
                                            }
                                        </Table.Body>
                                    </Table>
                                </Segment>
                            </Grid.Column>
                            <Grid.Column as='h2' verticalAlign='middle'>
                                {team.score}
                            </Grid.Column>
                        </Grid.Row>
                    )
                }
            </Grid>

            <Button onClick={() => {
                moveToJoinGameOption(props.history)
            }}
                    inverted color='red'>Back to join</Button>

        </div>
    )
};

export default Summary;