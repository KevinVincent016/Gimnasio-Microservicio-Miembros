package co.analisys.gimnasio.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import co.analisys.gimnasio.model.Miembro;
import co.analisys.gimnasio.service.MiembroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/members")
public class MiembroController {

    @Autowired
    private MiembroService miembroService;

    @Operation(
        summary = "Registrar un nuevo miembro",
        description = "Este endpoint permite registrar un nuevo miembro en el sistema. " +
        "Debes proporcionar el nombre, correo electrónico y teléfono del miembro. " +
        "Retorna la información del miembro creado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Miembro registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "403", description = "Prohibido"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public Miembro registrarMiembro(@RequestBody Miembro miembro) {
        return miembroService.registrarMiembro(miembro);
    }

    @Operation(
        summary = "Obtener todos los miembros",
        description = "Este endpoint recupera una lista completa de todos los miembros registrados en el sistema. " +
        "No requiere parámetros y retorna los detalles de cada miembro."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de miembros recuperada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "403", description = "Prohibido"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TRAINER')")
    public List<Miembro> obtenerTodosMiembros() {
        return miembroService.obtenerTodosMiembros();
    }

    @Operation(
        summary = "Obtener un miembro por ID",
        description = "Este endpoint recupera la información de un miembro específico utilizando su ID único. " +
        "Debes proporcionar el ID del miembro en la ruta. Retorna los detalles del miembro si se encuentra."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Miembro recuperado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "403", description = "Prohibido"),
        @ApiResponse(responseCode = "404", description = "Miembro no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TRAINER')")
    public ResponseEntity<Miembro> obtenerMiembroPorId(@PathVariable Long id) {
        Miembro miembro = miembroService.obtenerMiembroPorId(id);
        if (miembro != null) {
            return ResponseEntity.ok(miembro);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Actualizar un miembro",
        description = "Este endpoint permite actualizar la información de un miembro existente. " +
        "Debes proporcionar el ID del miembro en la ruta y los nuevos datos en el cuerpo de la solicitud. " +
        "Retorna la información actualizada del miembro."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Miembro actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "403", description = "Prohibido"),
        @ApiResponse(responseCode = "404", description = "Miembro no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Miembro> actualizarMiembro(@PathVariable Long id, @RequestBody Miembro miembro) {
        Miembro actualizado = miembroService.actualizarMiembro(id, miembro);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Eliminar un miembro",
        description = "Este endpoint permite eliminar un miembro del sistema utilizando su ID. " +
        "Debes proporcionar el ID del miembro en la ruta. Retorna true si la eliminación fue exitosa, " +
        "o false si el miembro no fue encontrado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Miembro eliminado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "403", description = "Prohibido"),
        @ApiResponse(responseCode = "404", description = "Miembro no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Boolean> eliminarMiembro(@PathVariable Long id) {
        boolean eliminado = miembroService.eliminarMiembro(id);
        if (eliminado) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}