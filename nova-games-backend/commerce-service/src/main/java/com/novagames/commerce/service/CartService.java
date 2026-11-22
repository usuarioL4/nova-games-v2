package com.novagames.commerce.service;
import com.novagames.commerce.dto.CommerceDtos.*;
import com.novagames.commerce.entity.CartItem;
import com.novagames.commerce.repository.CartRepository;
import com.novagames.commerce.client.CatalogClient;
import com.novagames.common.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service public class CartService {
    private final CartRepository repo;
    private final CatalogClient catalog;
    public CartService(CartRepository r,CatalogClient c) {
        repo=r;
        catalog=c;
    }
    public List<CartLine> list(String owner) {
        return repo.findByOwner(owner).stream().map(i->{var p=catalog.product(i.productId);return new CartLine(p.id(),p.nombre(),p.precio(),i.cantidad,p.stock(),p.imagen());}).toList();
    }
    @Transactional public void put(String owner,Line d) {
        var p=catalog.product(d.productoId());
        if(p.stock()<d.cantidad())throw new BusinessException(409,"INSUFFICIENT_STOCK","No hay suficiente stock para "+p.nombre());
        var item=repo.findByOwnerAndProductId(owner,d.productoId()).orElseGet(CartItem::new);
        item.owner=owner;
        item.productId=d.productoId();
        item.cantidad=d.cantidad();
        repo.save(item);
    }
    @Transactional public void delete(String owner,Long id) {
        repo.deleteByOwnerAndProductId(owner,id);
    }
}
