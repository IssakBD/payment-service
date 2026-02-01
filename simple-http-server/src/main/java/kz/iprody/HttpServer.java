package kz.iprody;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpServer {
    public static void main(String[] args) throws IOException {
        // 1. Путь к директории с файлами (можно передать через аргументы)
        String staticDirArg = args.length > 0 ? args[0] : "static";

        // Для отладки
        System.out.println("Working directory: " + System.getProperty("user.dir"));
        System.out.println("Static dir argument: " + staticDirArg);

        // Приводим к абсолютному пути и нормализуем
        Path baseDir = Paths.get(staticDirArg).toAbsolutePath().normalize();

        // Fallback: если static не найден в корне, ищем в simple-http-server/static
        if (!Files.exists(baseDir)) {
            Path fallback = Paths.get(System.getProperty("user.dir"), "simple-http-server", "static")
                    .toAbsolutePath().normalize();
            if (Files.exists(fallback)) {
                System.out.println("Using fallback: " + fallback);
                baseDir = fallback;
            }
        }

        System.out.println("Serving files from: " + baseDir);

        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server started at http://localhost:8080");

        while (true) {
            Socket clientSocket = serverSocket.accept();

            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
                 OutputStream out = clientSocket.getOutputStream()) {

                // 2. Читаем первую строку запроса (она содержит метод и путь)
                String requestLine = in.readLine();
                if (requestLine == null) {
                    clientSocket.close();
                    System.out.println("Client disconnected");
                    continue;
                }

                System.out.println(requestLine);

                // Читаем остальные заголовки (пока просто игнорируем)
                String line;
                while ((line = in.readLine()) != null && !line.isEmpty()) {
                    // можно печатать для отладки: System.out.println(line);
                }

                // 3. Парсим метод и путь из первой строки
                // Формат: "GET /index.html HTTP/1.1"
                String[] parts = requestLine.split(" ");
                if (parts.length < 2) {
                    sendErrorResponse(out, "400 Bad Request",
                            "<h1>400 Bad Request</h1>");
                    continue;
                }

                String method = parts[0];
                String urlPath = parts[1];

                // Обрабатываем только GET-запросы
                if (!method.equals("GET")) {
                    sendErrorResponse(out, "405 Method Not Allowed",
                            "<h1>405 Method Not Allowed</h1>");
                    continue;
                }

                // 4. Если запрос на корень "/", отдаём index.html
                if (urlPath.equals("/")) {
                    urlPath = "/index.html";
                }

                // 5. Убираем начальный слэш, чтобы получить относительный путь
                String relativePath = urlPath.startsWith("/")
                        ? urlPath.substring(1)
                        : urlPath;

                // 6. Формируем полный путь к файлу
                Path requestedFile = baseDir.resolve(relativePath)
                        .toAbsolutePath()
                        .normalize();

                System.out.println("Looking for: " + requestedFile);

                // 7. ЗАЩИТА: проверяем, что файл внутри baseDir (защита от ../)
                if (!requestedFile.startsWith(baseDir)) {
                    sendErrorResponse(out, "403 Forbidden",
                            "<h1>403 Forbidden</h1><p>Access denied</p>");
                    continue;
                }

                // 8. Проверяем существование файла
                if (Files.exists(requestedFile) && !Files.isDirectory(requestedFile)) {
                    // Файл найден — читаем его как байты
                    byte[] fileContent = Files.readAllBytes(requestedFile);

                    // Определяем Content-Type по расширению
                    String contentType = getContentType(
                            requestedFile.getFileName().toString());

                    // Отправляем успешный ответ
                    sendSuccessResponse(out, contentType, fileContent);
                } else {
                    // Файл не найден
                    sendErrorResponse(out, "404 Not Found",
                            "<h1>404 Not Found</h1><p>File not found: "
                                    + relativePath + "<​/p>");
                }

            } catch (IOException e) {
                System.err.println("Error handling request: " + e.getMessage());
            } finally {
                clientSocket.close();
            }
        }
    }

    // Метод для отправки успешного ответа (200 OK)
    private static void sendSuccessResponse(OutputStream out,
                                            String contentType,
                                            byte[] content) throws IOException {
        // Формируем заголовки
        String headers = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + content.length + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";

        // Отправляем заголовки (как текст)
        out.write(headers.getBytes(StandardCharsets.UTF_8));

        // Отправляем тело (как байты)
        out.write(content);
        out.flush();
    }

    // Метод для отправки ошибки (404, 403 и т.д.)
    private static void sendErrorResponse(OutputStream out,
                                          String status,
                                          String message) throws IOException {
        byte[] content = message.getBytes(StandardCharsets.UTF_8);

        String headers = "HTTP/1.1 " + status + "\r\n" +
                "Content-Type: text/html; charset=UTF-8\r\n" +
                "Content-Length: " + content.length + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";

        out.write(headers.getBytes(StandardCharsets.UTF_8));
        out.write(content);
        out.flush();
    }

    // Метод для определения Content-Type по расширению файла
    private static String getContentType(String fileName) {
        String extension = "";
        int dotIndex = fileName.lastIndexOf(".");

        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            extension = fileName.substring(dotIndex + 1).toLowerCase();
        }

        switch (extension) {
            case "html":
            case "htm":
                return "text/html; charset=UTF-8";
            case "css":
                return "text/css; charset=UTF-8";
            case "js":
                return "application/javascript; charset=UTF-8";
            case "json":
                return "application/json; charset=UTF-8";
            case "png":
                return "image/png";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "gif":
                return "image/gif";
            case "svg":
                return "image/svg+xml";
            case "ico":
                return "image/x-icon";
            case "txt":
                return "text/plain; charset=UTF-8";
            default:
                return "application/octet-stream"; // бинарные файлы
        }
    }
}