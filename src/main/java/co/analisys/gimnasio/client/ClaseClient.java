package co.analisys.gimnasio.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "gimnasio-clases", url = "http://localhost:8082")
public interface ClaseClient {
    
}