import  React, {useEffect} from "react";
import { useStoreActions } from "easy-peasy";
import Login from "./login";
const client = require('../client');

const Start = (props) => {

    const setGameId = useStoreActions(actions => actions.setGameId);
    const moveToGameProgressScreen = useStoreActions(actions => actions.moveToGameProgressScreen);
    const requestLogin = useStoreActions(actions => actions.requestLogin);

    function checkActiveGame() {

        client({method: 'GET', path: '/game'}).done(response => {
            setGameId(response.entity);
            moveToGameProgressScreen(props.history);
        }, () => {
            console.log('NewGameScreen');
            props.history.push({pathname: '/create/'})
        });
    }

    useEffect(() => {
        console.log('componentDidMount');
        requestLogin();
        checkActiveGame();
    });

    return (
        <div>
            <Login/>
            <h1>Loading..</h1>
        </div>
    )
};

export default Start;