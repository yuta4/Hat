const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');
import {Redirect, Route, BrowserRouter, Router} from "react-router-dom";

// const createRoutes = () => (
//     <Router>
//         <Route exact path="/teams" component={TeamFormation}/>
//         <Route exact path="/create" component={NewGame}/>
//     </Router>
// );
//
// export default createRoutes;


class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {next: ''}
    }

    redirectToInitScreen(response) {
        console.log('redirectToInitScreen ' + response);
        console.log('response ' + response.status.code + ' ' + response.entity);
        if (response.status.code == 200) {
            console.log('TeamFormation');
            this.setState({next: 'TeamFormation', gameId: response.entity})
        } else {
            console.log('NewGameScreen');
            this.setState({next: 'NewGameScreen'})
        }
    }

    componentDidMount() {
        console.log('componentDidMount');
        client({method: 'GET', path: '/game'}).done(response => {
            this.redirectToInitScreen(response);
        });
    }

    render() {
        console.log('1 rendering ' + this.state.next);
        if (this.state.next === 'TeamFormation') {
            const gid = this.state.gameId;
            return (
                <Redirect to={{pathname: '/teams', state: {id: gid} }}/>
            )
        } else if(this.state.next === 'NewGameScreen') {
            return <Redirect to={{pathname: '/create'}}/>
        }
        return (
            <div><h1>Loading..</h1></div>
        )
    }
}

class TeamFormation extends React.Component {
    render() {
        console.log('id' + this.props.id);
        console.log('location' + this.props.location.id);
        return (
            <h1>TeamFormation {this.props.id}</h1>
        )
    }
}

class NewGame extends React.Component {
    render() {
        return (
            <h1>NewGameScreen</h1>
        )
    }
}

ReactDOM.render(
    <BrowserRouter>
        <Route exact path="/" component={App}/>
        <Route exact path="/teams" component={TeamFormation}/>
        <Route exact path="/create" component={NewGame}/>
     </BrowserRouter>,
    document.getElementById('react')
);