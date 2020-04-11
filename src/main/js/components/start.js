// import moveToGameProgressScreen from "../app";
import  React, {useEffect} from "react";
import { useStoreActions } from "easy-peasy";
const client = require('../client');

const Start = (props) => {

    const setGameId = useStoreActions(actions => actions.setGameId);
    const moveToGameProgressScreen = useStoreActions(actions => actions.moveToGameProgressScreen);

    function checkActiveGame() {

        client({method: 'GET', path: '/game'}).done(response => {
            setGameId(response.entity);
            moveToGameProgressScreen(props.history);
        }, () => {
            console.log('NewGameScreen');
            this.props.history.push({pathname: '/create/'})
        });
    }

    useEffect(() => {
        console.log('componentDidMount');
        checkActiveGame();
    });

    return (
        <div><h1>Loading..</h1></div>
    )
};

export default Start;