import {BrowserRouter, Route, Router} from "react-router-dom";
import {createStore, StoreProvider} from "easy-peasy";
import TeamFormation from "./components/teamFormation"
import Start from "./components/start"
import Init from "./components/init"
import JoinScreen from "./components/join";
import GenerateWords from "./components/generateWords";
import model from "./model"
import React from 'react';

const ReactDOM = require('react-dom');
import 'bootstrap/dist/css/bootstrap.min.css';
import 'semantic-ui-css/semantic.min.css';
import Round from "./components/round";
import Summary from "./components/summary";

const store = createStore(model);

ReactDOM.render(
    <StoreProvider store={store}>
        <BrowserRouter>
            <Route exact path="/" component={Init}/>
            <Route exact path="/summary/:gid" component={Summary}/>
            <Route exact path="/teams/:gid" component={TeamFormation}/>
            <Route exact path="/words/:gid" component={GenerateWords}/>
            <Route exact path="/round/1/:gid" component={Round}/>
            <Route exact path="/round/2/:gid" component={Round}/>
            <Route exact path="/round/3/:gid" component={Round}/>
            <Route exact path="/start" component={Start}/>
            <Route exact path="/join" component={JoinScreen}/>
        </BrowserRouter>
    </StoreProvider>,
    document.getElementById('react')
);