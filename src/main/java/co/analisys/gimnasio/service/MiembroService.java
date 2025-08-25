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

    public Miembro obtenerMiembroPorId(Long id) {
        return miembroRepository.findById(id).orElse(null);
    }

    public Miembro actualizarMiembro(Long id, Miembro miembroActualizado) {
        return miembroRepository.findById(id).map(miembro -> {
            miembro.setNombre(miembroActualizado.getNombre());
            miembro.setEmail(miembroActualizado.getEmail());
            miembro.setFechaInscripcion(miembroActualizado.getFechaInscripcion());
            return miembroRepository.save(miembro);
        }).orElse(null);
    }

    public boolean eliminarMiembro(Long id) {
        if (miembroRepository.existsById(id)) {
            miembroRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
