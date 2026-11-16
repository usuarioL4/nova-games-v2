package com.novagames.catalog.mapper;
import com.novagames.catalog.entity.Product;
import com.novagames.catalog.dto.CatalogDtos.*;
import java.util.*;
public class ProductMapper {
    public static ProductView view(Product p) {
        return new ProductView(p.id,p.nombre,p.categoria,p.precio,p.stock,p.imagen,p.descripcion,Set.copyOf(p.plataformas));
    }
    public static void update(Product p,ProductInput d) {
        p.nombre=d.nombre();
        p.categoria=d.categoria();
        p.precio=d.precio();
        p.stock=d.stock();
        p.imagen=d.imagen();
        p.descripcion=d.descripcion();
        p.plataformas.clear();
        p.plataformas.addAll(d.plataformas());
    }
}
