package com.novagames.commerce.mapper;
import com.novagames.commerce.dto.CommerceDtos.*;
import com.novagames.commerce.entity.Purchase;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
public class PurchaseMapper {
    public static OrderView view(Purchase p,ObjectMapper json) {
        try {
            if(p.resultJson==null)return new OrderView(p.id,p.estado,List.of(),null,p.fecha);
            var result=json.readValue(p.resultJson,ReserveResult.class);
            return new OrderView(p.id,p.estado,result.items(),result.total(),p.fecha);
        }
        catch(Exception e) {
            throw new IllegalStateException("No se pudo leer el pedido",e);
        }
    }
}
