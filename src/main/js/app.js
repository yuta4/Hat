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
    render() {
        const players = this.props.players.map(player =>
            <Player key={player.email} data={player}/>
        );
        return (
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
)