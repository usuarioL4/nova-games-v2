import { useState } from "react";
import {Form,Button,Alert,Card} from 'react-bootstrap';
import { crearProducto } from "../../services/catalog";

function ProductoForm({token,onProductoCreado}){
    const [datos,setDatos] = useState({
        nombre: '',
        categoria: '',
        precio: '',
        stock: '',
        imagen: '',
        descripcion: '',
        plataformas: ''
    })

    const [error,setError] = useState('');
    const [mensaje,setMensaje] = useState('');
    const [cargando,setCargando] = useState(false);
    function handleChange(e){
        const {name,value} = e.target;
        setDatos({
            ...datos,
            [name]:value
        });
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setError('');
        setMensaje('');
        setCargando(true);
        
        const producto = {
            ...datos,
            precio:Number(datos.precio),
            stock:Number(datos.stock),
            plataformas:datos.plataformas.split(',')
            .map(p=> p.trim())
            .filter(Boolean)
        };

        try{
            await crearProducto(producto,token);
            setMensaje('Producto registrado correctamente');
            onProductoCreado()
        } catch(err){
            setError(err.response?.data?.message || 'Error al crear producto');
        } finally{
            setCargando(false);
        }
    }

        const campos = [
        {name:'nombre',label:'Nombre',type:'text'},
        {name:'categoria',label:'Categoría',type:'text'},
        {name:'precio',label:'Precio',type:'number'},
        {name:'stock',label:'Stock',type:'number'},
        {name:'imagen',label:'Ruta de imagen',type:'text'},
        {name:'plataformas',label:'Plataformas (PC, Xbox)',type:'text'}
    ];
    return (
        <Card className="p-4">
            <h3>Agregar videojuego</h3>

            {error && <Alert variant="danger">{error}</Alert>}
            {mensaje && <Alert variant="success">{mensaje}</Alert>}
            <Form onSubmit={handleSubmit}>
                {campos.map(campo => (
                <Form.Group className="mb-3" key={campo.name}>
                    <Form.Label>{campo.label}</Form.Label>
                    <Form.Control
                        name={campo.name}
                        type={campo.type}
                        min={campo.type === 'number' ? 0 : undefined}
                        step={campo.type === 'number' ? 1 : undefined}
                        value={datos[campo.name]}
                        onChange={handleChange}
                        required
                    
                    
                    />
                </Form.Group>
                ))}

                <Form.Group className="mb-3">
                    <Form.Label>Descripcion</Form.Label>
                    <Form.Control
                        as="textarea"
                        rows={3}
                        name="descripcion"
                        value={datos.descripcion}
                        onChange={handleChange}
                        required
                    
                    />
                </Form.Group>

                <Button type="submit" disabled={cargando}>
                    {cargando ? 'Guardando...' : 'Guardar Producto'}
                </Button>
            </Form>
        </Card>
    );
}

export default ProductoForm;