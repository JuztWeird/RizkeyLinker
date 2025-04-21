package project;
import java.io.*;
import java.net.*;
import java.util.*;

public class SimpleServer {
    private static final int PORT = 5050;
    private static HashMap<String, String> textLinkMap = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleRequest(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleRequest(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String requestLine = reader.readLine();
            if (requestLine != null && requestLine.startsWith("POST /process")) {
                StringBuilder payload = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null && !line.isEmpty()) { }
                while (reader.ready() && (line = reader.readLine()) != null) {
                    payload.append(line);
                }

                String json = payload.toString();
                json = json.replace("[", "").replace("]", ""); 
                String[] entries = json.split("},\\{"); 

                for (String entry : entries) {
                    entry = entry.replace("{", "").replace("}", "").replace("\"", ""); 
                    String[] keyValuePairs = entry.split(",");

                    String text = "", link = "";
                    for (String pair : keyValuePairs) {
                        String[] keyValue = pair.split(":");
                        if (keyValue[0].trim().equals("text")) {
                            text = keyValue[1].trim();
                        } else if (keyValue[0].trim().equals("link")) {
                            link = keyValue[1].trim();
                        }
                    }

                    if (!text.isEmpty() && !link.isEmpty()) {
                        textLinkMap.put(text, link);
                    }
                }

                System.out.println("Stored Data: " + textLinkMap);

                writer.println("Content-Type: text/plain");
                writer.println();
                writer.println("Data Received Successfully");
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
