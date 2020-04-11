import {useStoreState} from "easy-peasy";
import {Label} from "semantic-ui-react";
import React from "react";

const Login = () => {

    const login = useStoreState(state => state.login);

    return (
        <Label color='blue' horizontal>{login}</Label>
    )
};

export default Login;