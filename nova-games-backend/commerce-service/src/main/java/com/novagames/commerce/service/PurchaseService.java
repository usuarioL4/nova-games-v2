package com.novagames.commerce.service;
import com.novagames.commerce.dto.CommerceDtos.*;
import com.novagames.commerce.entity.Purchase;
import com.novagames.commerce.repository.PurchaseRepository;
import com.novagames.commerce.mapper.PurchaseMapper;
import com.novagames.commerce.client.CatalogClient;
import com.novagames.common.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.util.*;
@Service public class PurchaseService {
    private final PurchaseRepository repo;
    private final CatalogClient catalog;
    private final ObjectMapper json;
    public PurchaseService(PurchaseRepository r,CatalogClient c,ObjectMapper j) {
        repo=r;
        catalog=c;
        json=j;
    }
    // El identificador de idempotencia se conserva al reintentar. Catalog aplica el descuento una sola vez.
    public OrderView checkout(String owner,String token,UUID id,Checkout d)throws Exception {
        var quantities=new TreeMap<Long,Integer>();
        for(Line l:d.items())quantities.merge(l.productoId(),l.cantidad(),Integer::sum);
        if(quantities.values().stream().anyMatch(q->q>1000))throw new BusinessException(400,"INVALID_QUANTITY","Cantidad máxima por producto: 1000");
        String signature=json.writeValueAsString(quantities);
        var p=repo.findById(id).orElseGet(()->{var n=new Purchase();n.id=id;n.owner=owner;n.requestJson=signature;return repo.saveAndFlush(n);});
        if(!p.owner.equals(owner)||!p.requestJson.equals(signature))throw new BusinessException(409,"IDEMPOTENCY_CONFLICT","Usa una nueva clave para una compra diferente");
        if("CONFIRMED".equals(p.estado))return PurchaseMapper.view(p,json);
        var lines=quantities.entrySet().stream().map(e->new Line(e.getKey(),e.getValue())).toList();
        var result=catalog.reserve(new ReserveRequest(id,lines),token);
        p.resultJson=json.writeValueAsString(result);
        p.estado="CONFIRMED";
        return PurchaseMapper.view(repo.saveAndFlush(p),json);
    }
    public List<OrderView> list(String owner) {
        return repo.findByOwnerOrderByFechaDesc(owner).stream().map(p->PurchaseMapper.view(p,json)).toList();
    }
    public List<OrderView> all() {
        return repo.findAllByOrderByFechaDesc().stream().map(p->PurchaseMapper.view(p,json)).toList();
    }
    public OrderView get(String owner,UUID id) {
        return repo.findById(id).filter(p->p.owner.equals(owner)).map(p->PurchaseMapper.view(p,json)).orElseThrow(()->BusinessException.missing("ORDER_NOT_FOUND","Pedido no encontrado"));
    }
}
