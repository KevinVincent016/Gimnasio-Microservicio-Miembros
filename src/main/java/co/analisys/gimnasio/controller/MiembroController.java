package co.analisys.gimnasio.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import co.analisys.gimnasio.model.Miembro;
import co.analisys.gimnasio.service.MiembroService;

@RestController
@RequestMapping("/api/gimnasio/miembros")
public class MiembroController {

    @Autowired
    private MiembroService miembroService;
    
    
    @PostMapping("/miembros")
    public Miembro registrarMiembro(@RequestBody Miembro miembro) {
        return miembroService.registrarMiembro(miembro);
    }

    @GetMapping("/miembros")
    public List<Miembro> obtenerTodosMiembros() {
        return miembroService.obtenerTodosMiembros();
    }
    
}
