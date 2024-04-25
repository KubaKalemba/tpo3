package zad1;
import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RequestHandler {
    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public RequestHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Error creating input/output streams");
            return;
        }
    }

    public void handleRequest() {
        try {
            Request request = (Request) in.readObject();
            String wordToTranslate = request.getWordToTranslate();
            String targetLanguageCode = request.getTargetLanguageCode();
            //System.out.println("WORD: " +wordToTranslate);
            //System.out.println("LANG: " + targetLanguageCode);

            // Find the corresponding language server
            LanguageServer languageServer = findLanguageServer(targetLanguageCode);
            System.out.println(languageServer.getLanguageCode());

            //language server opens and listens on its port
            languageServer.run();

            //request is send to the language server
            try (Socket socket = new Socket("localhost", languageServer.getPORT());
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                 out.writeObject(request);
                 out.flush();

                 String translation = (String) in.readObject();

                 //JOptionPane.showMessageDialog(frame, translation);

            } catch (IOException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            //language server returns the translation

            //translation is passed back to client GUI

            // Send request to language server
            languageServer.sendRequest(wordToTranslate, clientSocket);
//
//            // Wait for response from language server
            String translation = languageServer.getTranslation(wordToTranslate);

            // Send response to client
            out.writeObject(translation);
            out.flush();
        } catch (IOException e) {
            System.out.println("Error handling request");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing streams");
            }
        }
    }

    private LanguageServer findLanguageServer(String targetLanguageCode) {
        for(LanguageServer languageServer : MainServer.languageServers) {
            if(languageServer.getLanguageCode().equals(targetLanguageCode)) {
                return languageServer;
            }
        }
        int currPort = MainServer.getCurrPort();
        MainServer.incrementCurrPort();
        LanguageServer languageServer = new LanguageServer(targetLanguageCode, currPort);
        MainServer.addServer(languageServer);
        return languageServer;
    }

    @Override
    public String toString() {
        return "RequestHandler{" +
                "clientSocket=" + clientSocket +
                ", in=" + in +
                ", out=" + out +
                '}';
    }
}
