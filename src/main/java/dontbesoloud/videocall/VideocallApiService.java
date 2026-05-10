package dontbesoloud.videocall;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.function.Consumer;

public class VideocallApiService extends WebSocketClient {
    private final Consumer<String> onMessageReceived;
    private final ObjectMapper mapper = new ObjectMapper();

    public VideocallApiService(URI serverUri, Consumer<String> onMessageReceived) {
        super(serverUri);
        this.onMessageReceived = onMessageReceived;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Подключено к серверу");
    }

    @Override
    public void onMessage(String message) {
        onMessageReceived.accept(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Соединение закрыто");
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public void sendVideoData(String base64Image, String sign) {
        if (isOpen()) {
            try {
                ObjectNode node = mapper.createObjectNode();
                node.put("type", "video");
                node.put("data", base64Image);
                node.put("sign", sign);
                send(mapper.writeValueAsString(node));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void skip() {
        if (isOpen()) {
            try {
                ObjectNode node = mapper.createObjectNode();
                node.put("type", "skip");
                send(mapper.writeValueAsString(node));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}