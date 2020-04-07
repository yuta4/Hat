import select from 'react-select';

const React = require('react');
const client = require('../client');

class TeamFormation extends React.Component {

    constructor(props) {
        console.log("TeamFormation constructor")
        super(props);
        this.state = {playersAvailable: []}
        this.handleNewPlayer = this.handleNewPlayer.bind(this)
    }

    componentDidMount() {
        console.log('calling /players')
        client({method: 'GET', path: '/players'}).done(response => {
            const items = [];
            response.entity.map((data, index) => {

                items.push(<option key={index}>{data}</option>);
            });
            this.setState({playersAvailable: items});
        });
    }

    handleNewPlayer(event) {
        const selected = event.target.value;
        // this.setState({playersAvailable: })
    }


    render() {
        console.log('TeamFormation render id ' + this.state.playersAvailable + ',' + this.props.match.params.gid);
        // const opts = this.state.playersAvailable;
        return (
            <div>
                <h1>TeamFormation {this.props.match.params.gid}</h1>
                <Team playersAvailable={this.state.playersAvailable} handleNewPlayer={this.handleNewPlayer}/>
            </div>
        )
    }
}

class Team extends React.Component {

    constructor(props) {
        super(props);

    }

    render() {
        return (
            <NewPlayer playersAvailable={this.props.playersAvailable} handleNewPlayer={this.props.handleNewPlayer}/>
        );
    }
}

class Player extends React.Component {

    constructor(props) {
        super(props);
        this.state = {email: string}
    }

    render() {

    }
}

class NewPlayer extends React.Component {

    render() {
        const opts = this.props.playersAvailable;
        return <select onClick={event => this.props.handleNewPlayer(event)}>{opts}</select>
    }
}

export default TeamFormation;