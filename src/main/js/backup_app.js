const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');

class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {players: []};
    }

    componentDidMount() {
        client({method: 'GET', path: '/players'}).done(response => {
            this.setState({players: response.entity});
        });
    }

    render() {
        return (
            <PlayerList players={this.state.players}/>
        )
    }
}

class PlayerList extends React.Component{

    constructor(props) {
        super(props);
        this.state = {gameId: []};
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleSubmit(e) {
        e.preventDefault();
        client({method: 'POST', path: '/game/create'}).done(response => {
            this.setState({gameId: response.entity});
        });
    }

    render() {
        const players = this.props.players.map(player =>
            <Player key={player.email} data={player}/>
        );
        return (
            <div>
            <table>
                <tbody>
                <tr>
                    <th>Id</th>
                    <th>Name</th>
                    <th>Email</th>
                </tr>
                {players}
                </tbody>
            </table>

                <h2>"Create new game"</h2>
                <form>
                    <button onClick={this.handleSubmit}>Create</button>
                </form>
            </div>
        )
    }
}

class Player extends React.Component{
    render() {
        return (
            <tr>
                <td>{this.props.data.id}</td>
                <td>{this.props.data.name}</td>
                <td>{this.props.data.email}</td>
            </tr>
        )
    }
}

ReactDOM.render(
    <App />,
    document.getElementById('react')
);