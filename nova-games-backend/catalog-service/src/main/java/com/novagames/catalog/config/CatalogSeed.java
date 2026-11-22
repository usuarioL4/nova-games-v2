package com.novagames.catalog.config;
import com.novagames.catalog.entity.Product;
import com.novagames.catalog.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import java.math.BigDecimal;
import java.util.*;
@Configuration public class CatalogSeed {
    // Carga 10 videojuegos de ejemplo. Solo inserta los que aún no existen (por nombre), así que no se duplican al reiniciar.
    @Bean CommandLineRunner catalogoInicial(ProductRepository repo,@Value("${CATALOG_SEED:true}") boolean enabled) {
        return args-> {
            if(!enabled)return;
            Set<String> existentes=new HashSet<>();
            repo.findAll().forEach(p->existentes.add(p.nombre.toLowerCase(Locale.ROOT)));
            List<Product> nuevos=new ArrayList<>();
            add(nuevos,existentes,"Shadow Strike: Cyber War","Acción",39990,15,"/imagenes/game/772415.png","Shooter táctico en una ciudad dominada por corporaciones. Infíltrate, hackea sistemas enemigos y completa misiones de alto riesgo solo o en cooperativo.",List.of("PC","Xbox"));
            add(nuevos,existentes,"Kingdoms of Eldoria","RPG",29990,23,"/imagenes/game/821031.png","Un RPG de mundo abierto donde reclutas héroes, forjas alianzas entre reinos y decides el destino de Eldoria a través de decisiones con consecuencias.",List.of("PC","Xbox"));
            add(nuevos,existentes,"Speed Legends X","Carreras",24990,18,"/imagenes/game/1735056.png","Carreras arcade a toda velocidad con más de 60 autos, circuitos urbanos y modo online competitivo para hasta 16 jugadores.",List.of("PC"));
            add(nuevos,existentes,"Nightmare Protocol","Terror",34990,12,"/imagenes/game/battlefield-background-r8xd4dc7f4a327ii.png","Terror psicológico en una instalación abandonada. Explora, resuelve acertijos y sobrevive a lo que despertó el experimento.",List.of("Xbox"));
            add(nuevos,existentes,"Galactic Frontiers","Ciencia ficción",44990,9,"/imagenes/game/game-image(1).png","Explora galaxias, construye tu flota y negocia con civilizaciones alienígenas en una aventura espacial de exploración y estrategia.",List.of("PC","Xbox"));
            add(nuevos,existentes,"Pixel Warriors Arena","Lucha",19990,30,"/imagenes/game/HYnDUL.png","Juego de lucha con estética pixel art, combos rápidos, 24 personajes y torneos locales y online.",List.of("PC","Xbox"));
            add(nuevos,existentes,"Dungeon Quest: Lost Souls","Aventura",27990,16,"/imagenes/game/kliff-from-crimson-desert-over-a-black-and-red-background.png","Aventura de acción por mazmorras malditas con jefes épicos, botín aleatorio y un sistema de combate exigente.",List.of("PC","Xbox"));
            add(nuevos,existentes,"Battlefield Zero: Last Stand","Shooter",49990,11,"/imagenes/game/tomb_raider_by_byju84_dc28mxm-pre.png","Combates a gran escala con vehículos, destrucción del entorno y campañas cooperativas para defender la última línea del frente.",List.of("PC","Xbox"));
            add(nuevos,existentes,"Monster Tamer Chronicles","RPG",32990,21,"/imagenes/game/1735056.png","Captura, entrena y evoluciona criaturas únicas mientras recorres un mundo lleno de misiones, torneos y secretos por descubrir.",List.of("PC","Xbox"));
            add(nuevos,existentes,"Neon Drift: Underground","Carreras",22990,25,"/imagenes/game/HYnDUL.png","Carreras callejeras nocturnas con derrapes, personalización completa de autos y bandas rivales que dominan cada barrio.",List.of("Xbox"));
            if(!nuevos.isEmpty())repo.saveAll(nuevos);
        }
        ;
    }
    private static void add(List<Product> lista,Set<String> existentes,String nombre,String categoria,long precio,int stock,String imagen,String descripcion,List<String> plataformas) {
        if(existentes.contains(nombre.toLowerCase(Locale.ROOT)))return;
        Product p=new Product();
        p.nombre=nombre;
        p.categoria=categoria;
        p.precio=BigDecimal.valueOf(precio);
        p.stock=stock;
        p.imagen=imagen;
        p.descripcion=descripcion;
        p.plataformas.addAll(plataformas);
        lista.add(p);
    }
}
