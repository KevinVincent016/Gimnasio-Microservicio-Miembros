# Microservicio de Miembros

Este microservicio maneja la gestión de miembros del gimnasio.

## Configuración

- **Puerto**: 8083
- **Base de datos**: H2 en memoria
- **Consola H2**: <http://localhost:8083/h2-console>

## Funcionalidades

- Gestión CRUD de miembros
- Información personal y de contacto
- Membresías y suscripciones
- Historial de asistencia

## Tecnologías

- Spring Boot 3.3.2
- Spring Data JPA
- H2 Database
- Java 17
- Maven

## Notificaciones por RabbitMQ

Cuando se registra un nuevo miembro, el servicio publica un evento en RabbitMQ para que otros microservicios (por ejemplo, notificaciones por email) reaccionen.

- Exchange: `miembros.notifications` (topic)
- Routing key: `miembros.signup`
- Queue declarada: `miembros.signup.notifications`
- Payload JSON:

```json
{
	"id": 1,
	"nombre": "Juan Perez",
	"email": "juan@example.com",
	"fechaInscripcion": "2025-09-15"
}
```

### Cómo ejecutar RabbitMQ

1. Levanta RabbitMQ con Docker Compose:

```bash
docker compose -f docker-compose.rabbitmq.yml up -d
```

2. Accede al panel: <http://localhost:15672> (user: guest, pass: guest)

3. Inicia la app y prueba registrar un miembro con POST a `<http://localhost:8083/api/members>`.

Puedes desactivar el envío de notificaciones con `notifications.enabled=false` en `application.properties`.
