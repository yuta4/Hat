import {useStoreState} from "easy-peasy";
import React from "react";
import {Col, Row} from "react-bootstrap";

const Login = () => {

    const login = useStoreState(state => state.login);

    return (
        <Row className="float-right">
            <Col>{login}</Col>
        </Row>
    )
};

export default Login;