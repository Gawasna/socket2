package bai2;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 9999);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter your username:");
            String username = scanner.nextLine();
            writer.println(username);

            Thread readThread = new Thread(() -> {
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            readThread.start();

            String message;
            while (true) {
                message = scanner.nextLine();
                writer.println(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
