import api from './api';

export async function listarProductos() {
    const response = await api.get('/api/productos')
    return response.data;
}

export async function crearProducto(producto,token) {
    const response = await api.post('/api/productos',producto,{
        headers:{
            Authorization: `Bearer ${token}`
        }
    });
    return response.data;

}
