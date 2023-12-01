package ca.concordia.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class MultiThread extends WebServer implements Runnable {
    private Socket clientSocket;
    private Account Account;
    private Semaphore mutex;

    public MultiThread(Socket socket, Account client) {
        this.clientSocket = socket;
        this.Account = client;
        mutex = new Semaphore(1);
    }

    @Override
    public void run() {
        BufferedReader in = null;
        OutputStream out = null;

        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = clientSocket.getOutputStream();

            String request = in.readLine();
            if (request != null) {

                mutex.acquire();
                if (request.startsWith("GET")) {
                    handleGetRequest(out, Account);
                } else if (request.startsWith("POST")) {
                    handlePostRequest(in, out, Account);
                }
                mutex.release();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {

            e.printStackTrace();
        } finally {
            // closing threads
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
                if (clientSocket != null)
                    clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
