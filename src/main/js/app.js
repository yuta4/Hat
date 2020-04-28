import {BrowserRouter, Route, Router} from "react-router-dom";
import {createStore, StoreProvider} from "easy-peasy";
import TeamFormation from "./components/teamFormation"
import NewGame from "./components/newGame"
import Start from "./components/start"
import JoinScreen from "./components/join";
import GenerateWords from "./components/generateWords";
import model from "./model"
import React from 'react';

const ReactDOM = require('react-dom');
import 'bootstrap/dist/css/bootstrap.min.css';
import 'semantic-ui-css/semantic.min.css';
import Round from "./components/round";

const store = createStore(model);

ReactDOM.render(
    <StoreProvider store={store}>
        <BrowserRouter>
            <Route exact path="/" component={Start}/>
            <Route exact path="/teams/:gid" component={TeamFormation}/>
            <Route exact path="/words/:gid" component={GenerateWords}/>
            <Route exact path="/first/:gid" component={Round}/>
            <Route exact path="/create" component={NewGame}/>
            <Route exact path="/join" component={JoinScreen}/>
        </BrowserRouter>
    </StoreProvider>,
    document.getElementById('react')
);