import { Container } from "react-bootstrap";
import ProductoForm from "../components/admin/ProductoForm";

function AdminProductoPage({token,onProductoCreado}){
    return (
        <Container className="my-4">
            <h2>Administracion de productos</h2>
            <ProductoForm token={token}
              onProductoCreado={onProductoCreado}/>
        </Container>
    );
}

export default AdminProductoPage;