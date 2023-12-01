package ca.concordia.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

//create the WebServer class to receive connections on port 5000. Each connection is handled by a master thread that puts the descriptor in a bounded buffer. A pool of worker threads take jobs from this buffer if there are any to handle the connection.
public class WebServer {
    
    public void start() throws java.io.IOException {

        ServerSocket serverSocket = null;
        try {
            // Create a server socket
            serverSocket = new ServerSocket(5000);
            while (true) {
                System.out.println("Waiting for a client to connect...");
                // Accept a connection from a client
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client...");

                new Thread(new MultiThread(clientSocket, initialzeaccount())).start();
            }
        } finally {
            serverSocket.close();
        }

    }

    protected static Account initialzeaccount() throws IOException {
        String file = "webserver/src/main/resources/accounts.txt";


        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line = reader.readLine();

        if (line == null) {
            reader.close();
            throw new NoSuchElementException("No more accounts");
        }
        String[] parts = line.split(",");
        if (parts.length != 2) {
            reader.close();
            throw new IllegalArgumentException("line should have 2 values only");
        }

        int B = Integer.parseInt(parts[0].trim());
        String id = parts[1].trim();

        AtomicInteger Balance = new AtomicInteger(B);
        reader.close();
        return new Account(Balance, id);
    }

    protected static void handleGetRequest(OutputStream out, Account C) throws IOException {
        // Respond with a basic HTML pageB
        System.out.println("Handling GET request");
        String response = "HTTP/1.1 200 OK\r\n\r\n" +
        "<!DOCTYPE html>\n" +
        "<html>\n" +
        "<head>\n" +
        "<title>Concordia Transfers</title>\n" +
        "</head>\n" +
        "<body>\n" +
        "<h1>Welcome to Concordia Transfers</h1>\n" +
        "<p>Select the account and amount to transfer</p>\n" +
        "<form action=\"/submit\" method=\"post\">\n" +
        "        <label for=\"account\">Account:</label>\n" +
        "        <input type=\"text\" id=\"account\" name=\"account\" value=\"" + C.getID() + "\"><br><br>\n" +
        "        <label for=\"value\">Value:</label>\n" +
        "        <input type=\"text\" id=\"value\" name=\"value\" value=\"" + C.getBalance() + "\"><br><br>\n" +
        "        <label for=\"toAccount\">To Account:</label>\n" +
        "        <input type=\"text\" id=\"toAccount\" name=\"toAccount\"><br><br>\n" +
        "        <label for=\"toValue\">To Value:</label>\n" +
        "        <input type=\"text\" id=\"toValue\" name=\"toValue\"><br><br>\n" +
        "        <input type=\"submit\" value=\"Submit\">\n" +
        "    </form>\n" +
        "</body>\n" +
        "</html>\n";

out.write(response.getBytes());
out.flush();
    }

    protected static void handlePostRequest(BufferedReader in, OutputStream out, Account C) throws IOException {
        System.out.println("Handling post request");
        StringBuilder requestBody = new StringBuilder();
        int contentLength = 0;
        String line;

        // Read headers to get content length
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            if (line.startsWith("Content-Length")) {
                contentLength = Integer.parseInt(line.substring(line.indexOf(' ') + 1));
            }
        }

        // Read the request body based on content length
        for (int i = 0; i < contentLength; i++) {
            requestBody.append((char) in.read());
        }

        System.out.println(requestBody.toString());
        // Parse the request body as URL-encoded parameters
        String[] params = requestBody.toString().split("&");
        String toAccount = null, toValue = null;

        for (String param : params) {
            String[] parts = param.split("=");
            if (parts.length == 2) {
                String key = URLDecoder.decode(parts[0], "UTF-8");
                String val = URLDecoder.decode(parts[1], "UTF-8");

                switch (key) {
                    case "account":
                       C.setID(val);
                        break;
                    case "value":
                        C.withdraw(Integer.valueOf(val));
                        break;
                    case "toAccount":
                        toAccount = val;
                        break;
                    case "toValue":
                        C.deposit(Integer.valueOf(val));
                        toValue=val;
                        break;
                }
            }
        }
            
        // Create the response
        String responseContent = "<html><body><h1>Thank you for using Concordia Transfers</h1>" +
                "<h2>Received Form Inputs:</h2>" +
                "<p>Account: " + C.getID() + "</p>" +
                "<p>Value: " + C.getBalance() + "</p>" +
                "<p>To Account: " + toAccount + "</p>" +
                "<p>To Value: " + toValue + "</p>" +
                "</body></html>";

        // Respond with the received form inputs
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + responseContent.length() + "\r\n" +
                "Content-Type: text/html\r\n\r\n" +
                responseContent;

                System.out.println("Transfer Details:");
    System.out.println("From Account: " + C.getID());
    System.out.println("From Value: " + C.getBalance());
    System.out.println("To Account: " + toAccount);
    System.out.println("To Value: " + toValue);

        out.write(response.getBytes());
        out.flush();
    }

    public static void main(String[] args) {
        // Start the server, if an exception occurs, print the stack trace
        WebServer server = new WebServer();
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
