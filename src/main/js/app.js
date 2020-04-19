import {BrowserRouter, Route, Router} from "react-router-dom";
import {createStore, StoreProvider} from "easy-peasy";
import TeamFormation from "./components/teamFormation"
import NewGame from "./components/newGame"
import Start from "./components/start"
import JoinScreen from "./components/join";
import model from "./model"
import React from 'react';

const ReactDOM = require('react-dom');
import 'bootstrap/dist/css/bootstrap.min.css';

const store = createStore(model);

ReactDOM.render(
    <StoreProvider store={store}>
        <BrowserRouter>
            <Route exact path="/" component={Start}/>
            <Route exact path="/teams/:gid" component={TeamFormation}/>
            <Route exact path="/create" component={NewGame}/>
            <Route exact path="/join" component={JoinScreen}/>
        </BrowserRouter>
    </StoreProvider>,
    document.getElementById('react')
);