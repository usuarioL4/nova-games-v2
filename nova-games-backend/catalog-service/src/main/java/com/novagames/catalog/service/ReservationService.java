package com.novagames.catalog.service;
import com.novagames.catalog.dto.CatalogDtos.*;
import com.novagames.catalog.entity.*;
import com.novagames.catalog.repository.*;
import com.novagames.common.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;
@Service public class ReservationService {
    private final ProductRepository products;
    private final ReservationRepository reservations;
    private final ObjectMapper json;
    public ReservationService(ProductRepository p,ReservationRepository r,ObjectMapper j) {
        products=p;
        reservations=r;
        json=j;
    }
    @Transactional(rollbackFor=Exception.class) public ReserveResult reserve(String owner,ReserveRequest req)throws Exception {
        var quantities=new TreeMap<Long,Integer>();
        for(Line l:req.items())quantities.merge(l.productoId(),l.cantidad(),Integer::sum);
        String signature=json.writeValueAsString(quantities);
        var old=reservations.findById(req.pedidoId());
        if(old.isPresent())return existing(old.get(),owner,signature);
        Map<Long,Product> locked=new LinkedHashMap<>();
        for(Long id:quantities.keySet())locked.put(id,products.lock(id).orElseThrow(()->BusinessException.missing("PRODUCT_NOT_FOUND","Producto no encontrado: "+id)));
        old=reservations.findById(req.pedidoId());
        if(old.isPresent())return existing(old.get(),owner,signature);
        List<ReservedLine> lines=new ArrayList<>();
        BigDecimal total=BigDecimal.ZERO;
        for(var entry:quantities.entrySet()) {
            Product p=locked.get(entry.getKey());
            int qty=entry.getValue();
            if(!p.activo)throw BusinessException.missing("PRODUCT_NOT_FOUND","Producto no disponible: "+p.nombre);
            if(p.stock<qty)throw new BusinessException(409,"INSUFFICIENT_STOCK","No hay suficiente stock para "+p.nombre);
            BigDecimal sub=p.precio.multiply(BigDecimal.valueOf(qty));
            lines.add(new ReservedLine(p.id,p.nombre,p.precio,qty,sub));
            total=total.add(sub);
            p.stock-=qty;
        }
        var result=new ReserveResult(req.pedidoId(),lines,total);
        Reservation r=new Reservation();
        r.id=req.pedidoId();
        r.owner=owner;
        r.requestJson=signature;
        r.resultJson=json.writeValueAsString(result);
        reservations.save(r);
        return result;
    }
    private ReserveResult existing(Reservation r,String owner,String signature)throws Exception {
        if(!r.owner.equals(owner)||!r.requestJson.equals(signature))throw new BusinessException(409,"IDEMPOTENCY_CONFLICT","La compra ya existe con otros datos");
        return json.readValue(r.resultJson,ReserveResult.class);
    }
}
