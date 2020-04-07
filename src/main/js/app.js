const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');
import {BrowserRouter, Route, Router} from "react-router-dom";
import TeamFormation from "./components/teamFormation"
import NewGame from "./components/newGame"

function moveToGameProgressScreen(gid, history) {
    client({method: 'GET', path: '/progress?gameId=' + gid}).done(response => {
        console.log('moveToGameProgressScreen ' + response.entity + gid);
        history.push({pathname: response.entity + gid})
    }, response => {
        console.log('moveToGameProgressScreen error ' + response.status);
    })
}

export default moveToGameProgressScreen;

class App extends React.Component {

    constructor(props) {
        super(props);
    }

    componentDidMount() {
        console.log('componentDidMount');
        this.checkActiveGame();
    }

    checkActiveGame() {
        client({method: 'GET', path: '/game'}).done(response => {
            moveToGameProgressScreen(response.entity, this.props.history);
        }, () => {
            console.log('NewGameScreen');
            this.props.history.push({pathname: '/create/'})
        });
    }

    render() {
        return (
            <div><h1>Loading..</h1></div>
        )
    }
}

ReactDOM.render(
    <BrowserRouter>
        <Route exact path="/" component={App}/>
        <Route exact path="/teams/:gid" component={TeamFormation}/>
        <Route exact path="/create" component={NewGame}/>
    </BrowserRouter>,
    document.getElementById('react')
);