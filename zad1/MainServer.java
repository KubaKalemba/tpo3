package zad1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainServer {

    private static final int PORT = 8080;
    protected static final List<LanguageServer> languageServers = new ArrayList<>();
    private static int currPort = 8081;
    private static Socket clientSocket;

    private ObjectInputStream clientIn;
    private ObjectOutputStream clientOut;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Starting main server on port " + PORT + "...");

        LanguageServer engServer = new LanguageServer("EN", currPort++);
        addServer(engServer);

        while (true) {
            clientSocket = serverSocket.accept();
            System.out.println("New client connected");

            // Handle client request
            RequestHandler requestHandler = new RequestHandler(clientSocket);
            System.out.println("cccc" + requestHandler);
            requestHandler.handleRequest();
        }
    }

    public static void addServer(LanguageServer languageServer) {
        languageServers.add(languageServer);
    }

    public static int getCurrPort() {
        return currPort;
    }

    public static void incrementCurrPort() {
        currPort++;
    }
}
