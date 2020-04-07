import Select from 'react-select';

const React = require('react');
const client = require('../client');

class TeamFormation extends React.Component {

    constructor(props) {
        console.log("TeamFormation constructor")
        super(props);
        this.state = {playersAvailable: []}
    }

    componentDidMount() {
        client({method: 'GET', path: '/players'}).done(response => {
            const items = [];
            response.entity.map((data) => {

                items.push(<option key={data} value={data}>{data}</option>);
                //here I will be creating my options dynamically based on
                //what props are currently passed to the parent component
            });
            this.setState({playersAvailable: items});

        });
    }

    render() {
        console.log('TeamFormation render id ' + this.props.match.params.gid);
        return (
            <div>
                <h1>TeamFormation {this.props.match.params.gid}</h1>
                //TODO: move to player level
                <Select options={this.state.playersAvailable}/>
            </div>
        )
    }
}

class Team extends React.Component {

    constructor(props) {
        super(props);

    }

    render() {

    }
}

class Player extends React.Component {

    constructor(props) {
        super(props);
        this.state = {email: string}
    }

    render() {
        <Select options={techCompanies}/>
    }
}

class NewPlayer extends React.Component {


    render() {

    }
}

export default TeamFormation;