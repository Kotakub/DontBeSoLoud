package dontbesoloud.general;

import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class ZmqService {
    private Thread listenerThread;
    private volatile boolean running = false;

    // Определяем интерфейс для получения двух параметров
    public interface ZmqDataHandler {
        void handle(String sign, String base64Image);
    }

    public void startListening(ZmqDataHandler handler) {
        if (running) return;
        running = true;

        listenerThread = new Thread(() -> {
            try (ZContext context = new ZContext()) {
                ZMQ.Socket subscriber = context.createSocket(ZMQ.SUB);
                subscriber.connect("tcp://127.0.0.1:5555");
                subscriber.subscribe("".getBytes());
                subscriber.setReceiveTimeOut(1000);

                while (running && !Thread.currentThread().isInterrupted()) {
                    // Используем DONTWAIT для плавности видео
                    String message = subscriber.recvStr(ZMQ.DONTWAIT);
                    if (message != null && message.contains("|")) {
                        String[] parts = message.split("\\|");
                        if (parts.length == 2) {
                            handler.handle(parts[0], parts[1]);
                        }
                    }
                    Thread.sleep(10); // Минимальная задержка
                }
            } catch (Exception e) {
                System.err.println("ZMQ Error: " + e.getMessage());
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public void stop() {
        running = false;
        if (listenerThread != null) listenerThread.interrupt();
    }
}