package co.analisys.gimnasio.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "gimnasio-entrenadores", url = "http://localhost:8081")
public interface EntrenadorClient {
    
}