# RyFtaxi

Proyecto Spring Boot para chat / WebSocket enfocado a funcionalidades de taxi (RyFtaxi).

## Resumen

Aplicación Java (Spring Boot) con soporte WebSocket para mensajería de chat en tiempo real entre usuarios (salas/rooms). Incluye controladores Web y un handler WebSocket personalizado (`ChatWebSocketHandler`).

## Requisitos

- JDK 17 (el proyecto usa toolchain Java 17 según `build.gradle`).
- Gradle (se incluye wrapper: `./gradlew`).
- Puerto por defecto: 8080 (configurado en `src/main/resources/application.yaml`).

## Estructura relevante

- `src/main/java/com/ryfsystems/ryftaxi/` - código fuente principal
  - `controller/` - `ChatController`, `ChatWebSocketHandler`, `HomeController`
  - `config/` - `WebConfig`, `WebSocketConfig`
  - `model/` - `ChatMessage`, `User`
  - `enums/` - `MessageType`, `UserType`
- `src/main/resources/` - recursos (templates, static, `application.yaml`)

## Endpoints importantes

- WebSocket endpoint: ws://<HOST>:8080/ws/chat
  - Configurado en `WebSocketConfig` (path `/ws/chat`).
  - Orígenes permitidos: `*` (permitido en desarrollo).

## Formato de mensajes (JSON)

El modelo `ChatMessage` contiene los campos siguientes (serialización JSON esperada):

- `type` (MessageType): `CHAT`, `JOIN`, `LEAVE`, `TYPING`
- `content` (String): texto del mensaje (si aplica)
- `sender` (String): nombre del remitente
- `roomId` (String): identificador de sala
- `timestamp` (String): timestamp ISO (generado por el servidor)

Ejemplo de mensaje para enviar:

{
  "type": "CHAT",
  "content": "Hola desde cliente",
  "sender": "alice",
  "roomId": "room-123"
}

## Desarrollo y ejecución local

1. Construir y ejecutar con Gradle wrapper:

```bash
./gradlew bootRun
```

2. Generar JAR ejecutable:

```bash
./gradlew clean bootJar
# El jar queda en build/libs/, p. ej. build/libs/ryftaxi-0.0.1-SNAPSHOT.jar
```

3. Ejecutar el JAR:

```bash
java -jar build/libs/ryftaxi-0.0.1-SNAPSHOT.jar
```

Para cambiar el puerto al ejecutar el JAR:

```bash
java -jar build/libs/ryftaxi-0.0.1-SNAPSHOT.jar --server.port=9090
```

4. Ejecutar tests:

```bash
./gradlew test
```

## Conexión WebSocket (ejemplo cliente JS)

```javascript
const url = 'ws://localhost:8080/ws/chat';
const ws = new WebSocket(url);

ws.onopen = () => {
  console.log('Conectado');
  // Mandar JOIN si el protocolo de la app lo requiere
  const joinMsg = JSON.stringify({ type: 'JOIN', sender: 'alice', roomId: 'room-123' });
  ws.send(joinMsg);
};

ws.onmessage = (evt) => {
  const msg = JSON.parse(evt.data);
  console.log('Recibido:', msg);
};

ws.onclose = () => console.log('Conexión cerrada');
ws.onerror = (e) => console.error('WS error', e);
```

## Despliegue

Opciones simples de despliegue:

- Ejecutar JAR en una VM o servidor: construir con `./gradlew bootJar` y ejecutar `java -jar ...`.
- Contenerizar (opcional): puedes crear un `Dockerfile` multistage que use `eclipse-temurin:17` como runtime y copiar el jar. (No incluido aquí para evitar cambios automáticos).

Consideraciones:
- Revisar `application.yaml` para ajustar `server.address`, `server.port` y logging.
- Asegurar CORS / allowed-origins en producción (no dejar `*` sin control).

## Notas y siguientes pasos recomendados

- Añadir tests de integración para el handler WebSocket.
- Añadir un `Dockerfile` y `docker-compose` para despliegue local reproducible.
- Habilitar seguridad (autenticación/autorization) si se exponen endpoints en producción.
