import {BrowserRouter, Route, Router} from "react-router-dom";
import {createStore, StoreProvider} from "easy-peasy";
import TeamFormation from "./components/teamFormation"
import NewGame from "./components/newGame"
import Start from "./components/start"
import model from "./model"
import React from 'react';

const ReactDOM = require('react-dom');
const client = require('./client');

const store = createStore(model);

ReactDOM.render(
    <StoreProvider store={store}>
        <BrowserRouter>
            <Route exact path="/" component={Start}/>
            <Route exact path="/teams/:gid" component={TeamFormation}/>
            <Route exact path="/create" component={NewGame}/>
        </BrowserRouter>
    </StoreProvider>,
    document.getElementById('react')
);