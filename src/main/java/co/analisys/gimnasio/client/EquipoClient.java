package co.analisys.gimnasio.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "gimnasio-equipos", url = "http://localhost:8084")
public interface EquipoClient {
    
}