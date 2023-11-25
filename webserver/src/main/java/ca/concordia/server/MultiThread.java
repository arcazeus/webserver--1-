package ca.concordia.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class MultiThread extends WebServer implements Runnable {



  private Socket clientSocket;

    public MultiThread(Socket socket) {
        this.clientSocket = socket;
    }

    
     @Override
      public void run(){
         BufferedReader in= null;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            
            e.printStackTrace();
        }
            OutputStream out= null;
            try {
                out = clientSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String request="";
            try {
                request = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (request != null) {
                if (request.startsWith("GET")) {
                    // Handle GET request
                    try {
                        handleGetRequest(out);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (request.startsWith("POST")) {
                    try {
                        handlePostRequest(in, out);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



