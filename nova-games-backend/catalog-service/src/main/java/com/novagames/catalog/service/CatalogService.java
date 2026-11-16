package com.novagames.catalog.service;
import com.novagames.catalog.dto.CatalogDtos.*;
import com.novagames.catalog.entity.*;
import com.novagames.catalog.mapper.ProductMapper;
import com.novagames.catalog.repository.*;
import com.novagames.common.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service public class CatalogService {
    private final ProductRepository repo;
    public CatalogService(ProductRepository r) {
        repo=r;
    }
    @Transactional(readOnly=true) public List<ProductView> list(String q,String categoria,String plataforma) {
        return repo.findByActivoTrueOrderById().stream().filter(p->q==null||p.nombre.toLowerCase(Locale.ROOT).contains(q.toLowerCase(Locale.ROOT))).filter(p->categoria==null||categoria.equalsIgnoreCase(p.categoria)).filter(p->plataforma==null||p.plataformas.stream().anyMatch(plataforma::equalsIgnoreCase)).map(ProductMapper::view).toList();
    }
    @Transactional(readOnly=true) public ProductView get(Long id) {
        return ProductMapper.view(repo.findById(id).filter(p->p.activo).orElseThrow(()->missing()));
    }
    @Transactional public ProductView create(ProductInput d) {
        Product p=new Product();
        ProductMapper.update(p,d);
        return ProductMapper.view(repo.save(p));
    }
    @Transactional public ProductView update(Long id,ProductInput d) {
        Product p=repo.lock(id).filter(x->x.activo).orElseThrow(()->missing());
        ProductMapper.update(p,d);
        return ProductMapper.view(p);
    }
    @Transactional public void delete(Long id) {
        Product p=repo.lock(id).filter(x->x.activo).orElseThrow(()->missing());
        p.activo=false;
    }
    private BusinessException missing() {
        return BusinessException.missing("PRODUCT_NOT_FOUND","Producto no encontrado");
    }
}
