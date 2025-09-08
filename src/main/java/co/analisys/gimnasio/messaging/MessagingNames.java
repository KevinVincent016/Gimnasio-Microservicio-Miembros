package co.analisys.gimnasio.messaging;

public final class MessagingNames {
    private MessagingNames() {}

    // Exchange
    public static final String EXCHANGE_GIMNASIO = "gimnasio.exchange";

    // Cola para eventos de clases (compartida por ahora)
    public static final String QUEUE_CLASES_EVENTS = "clases.events";

    // Routing keys
    public static final String RK_CLASE_CREATED = "clases.created";
    public static final String RK_CLASE_UPDATED = "clases.updated";
    public static final String RK_CLASE_DELETED = "clases.deleted";
}
