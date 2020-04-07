const React = require('react');
import moveToGameProgressScreen from "../app";
const client = require('../client');

class NewGame extends React.Component {

    constructor(props) {
        super(props);
        this.createGame = this.createGame.bind(this);
    }

    createGame() {
        client({method: 'POST', path: '/game/create'}).done(response => {
            console.log('NewGame ' + response.entity);
            moveToGameProgressScreen(response.entity, this.props.history)
        });
    }

    render() {
        return (
            <div>
                <h1>NewGameScreen</h1>
                <button onClick={this.createGame}>Create new game</button>
            </div>
        )
    }
}

export default NewGame;