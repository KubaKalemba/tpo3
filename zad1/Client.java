package zad1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {

    private final int port;

    public Client(int port) {
        this.port = port;
    }

    public String translate(String wordToTranslate, String targetLanguageCode) {
        try (Socket socket = new Socket("localhost", port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            Request request = new Request(wordToTranslate, targetLanguageCode);
            out.writeObject(request);
            out.flush();

            return (String) in.readObject();

        } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
}
