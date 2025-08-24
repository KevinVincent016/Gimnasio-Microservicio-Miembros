package co.analisys.gimnasio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import co.analisys.gimnasio.repository.MiembroRepository;
import co.analisys.gimnasio.model.Miembro;
import java.util.List;

@Service
public class MiembroService {

    @Autowired
    private MiembroRepository miembroRepository;

    public Miembro registrarMiembro(Miembro miembro) {
        return miembroRepository.save(miembro);
    }

    public List<Miembro> obtenerTodosMiembros() {
        return miembroRepository.findAll();
    }
}
