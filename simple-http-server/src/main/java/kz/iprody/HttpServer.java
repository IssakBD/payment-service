package kz.iprody;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server started on port 8080");
        while (true) {
            Socket clientSocket = serverSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            //Чтение запроса
            String line;
            while ((line = in.readLine())!= null && !line.isEmpty()) {
                System.out.println(line);
            }
            System.out.println();

            String response =
                    "<h1>Hello from server!</h1>";
            out.write("HTTP/1.1 200 OK\r\n");
            out.write("Content-Type: text/html; charset=UTF-8\r\n");
            out.write("Content-Length: " + response.length() + "\r\n");
            out.write("\r\n");
            out.write(response);
            out.flush();
            clientSocket.close();
        }
    }
}