import {useStoreState} from "easy-peasy";
import React from "react";
import {Col, Row} from "react-bootstrap";
import {Label} from "semantic-ui-react";

const Login = () => {

    const login = useStoreState(state => state.login);

    return (
        <Label color='teal' tag attached={"top right"}>
            {login}
        </Label>
    )
};

export default Login;