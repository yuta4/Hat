const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');
import {BrowserRouter, Route, Router, Switch} from "react-router-dom";


class App extends React.Component {

    constructor(props) {
        super(props);
    }

    componentDidMount() {
        console.log('componentDidMount');
        client({method: 'GET', path: '/game'}).done(response => {
            console.log('TeamFormation');
            this.props.history.push({pathname: '/teams/' + response.entity})
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

class TeamFormation extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        console.log('id ' + this.props.match.params.gid);
        return (
            <h1>TeamFormation {this.props.match.params.gid}</h1>
        )
    }
}

class NewGame extends React.Component {
    createGame() {
        client({method: 'POST', path: '/game/create'}).done(response => {
            console.log('NewGame' + response.entity);
        });
    }

    render() {
        return (
            <div>
            <h1>NewGameScreen</h1>
                <button onClick={this.createGame}>New game</button>
            </div>
        )
    }
}

ReactDOM.render(
    <BrowserRouter>
        <Switch>
            <Route exact path="/" component={App}/>
            <Route exact path="/teams/:gid" component={TeamFormation}/>
            <Route exact path="/create" component={NewGame}/>
        </Switch>
    </BrowserRouter>,
    document.getElementById('react')
);