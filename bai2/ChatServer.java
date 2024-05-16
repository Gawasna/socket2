package bai2;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {
    private static Map<String, PrintWriter> clients = new HashMap<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(9999);
            System.out.println("Server started. Listening on port 9999...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                Thread clientHandler = new Thread(new ClientHandler(clientSocket));
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader reader;
        private PrintWriter writer;
        private String username;

        public ClientHandler(Socket socket) {
            try {
                this.clientSocket = socket;
                this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                this.writer = new PrintWriter(clientSocket.getOutputStream(), true);

                // Request username from client
                writer.println("Enter your username:");
                this.username = reader.readLine();
                broadcast(username + " has joined the chat.", null);
                clients.put(username, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    broadcast(username + ": " + message, writer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                clients.remove(username);
                broadcast(username + " has left the chat.", null);
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void broadcast(String message, PrintWriter excludingWriter) {
        for (PrintWriter writer : clients.values()) {
            if (writer != excludingWriter) {
                writer.println(message);
            }
        }
    }
}
