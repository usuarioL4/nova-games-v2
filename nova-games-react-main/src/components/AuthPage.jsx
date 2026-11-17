import { useState } from "react";
import {Button,Alert} from 'react-bootstrap';
import Login from './Login'
import Register from './Registro'

function AuthPage(){
    const [vista,setVista] = useState('login')
    const [sesion,setSesion] = useState(null)

    function handleLogin(respuesta){
        setSesion(respuesta);
    }
    function cerrarSesion(){
        setSesion(null);
    }
    return(
        <div className="conhtainer my-4" id="auth">
            {sesion ? (
                <Alert variant="success">
                    <h4>Bienvenido, {sesion.user.username}</h4>
                    <p>Rol: {sesion.user.role}</p>
                    <Button
                      variant="outline-dark"
                      onClick={cerrarSesion}>
                        Cerrar sesion
                      </Button>
                </Alert>
            ) : (
                <>
                <div className="d-flex gap-2 justify-content-center">
                    <Button
                      variant={vista === 'login' ? 'primary' : 'outline-primary'}
                      onClick={() => setVista('login')}>
                        Login
                      </Button>
                      <Button
                      variant={vista === 'register' ? 'primary' : 'outline-primary'}
                      onClick={() => setVista('register')}>
                        Register
                      </Button>
                </div>
                {vista === 'login' ? (
                    <Login onLogin={handleLogin}/>
                ): (
                    <Register/>
                )}
                </>
            )}
        </div>
    );
}

export default AuthPage;