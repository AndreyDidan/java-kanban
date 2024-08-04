package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.ManagerSaveException;
import exception.ValidationException;
import task.manager.service.TaskManager;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler implements HttpHandler {

    TaskManager taskManager;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected boolean isEndpoint(String str) {
        return switch (str) {
            case "tasks", "subtasks", "epics", "history", "prioritized" -> true;
            default -> false;
        };
    }

    protected boolean isEndpointTask(String str) {
        return switch (str) {
            case "tasks", "subtasks", "epics" -> true;
            default -> false;
        };
    }

    protected boolean isInteger(String str) {
        try {
            int id = Integer.parseInt(str);
            if (id < 0) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try (exchange) {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            int length = pathParts.length;

            try (exchange) {
                try {
                    if (isEndpoint(pathParts[1])) {
                        switch (method) {
                            case "GET" -> {
                                if (length == 2 && isEndpoint(pathParts[1])) {
                                    handleGetRequest(exchange);
                                } else if (length == 3 && isEndpointTask(pathParts[1])) {
                                    if (isInteger(pathParts[2])) {
                                        handleGetRequest(exchange);
                                    } else {
                                        sendHasData(exchange, "id должен быть числом");
                                    }
                                } else if (length == 4 && pathParts[1].equals("epics") && pathParts[3].equals("subtasks")) {
                                    if (isInteger(pathParts[2])) {
                                        handleGetRequest(exchange);
                                    } else {
                                        sendHasData(exchange, "id должен быть числом");
                                    }
                                } else {
                                    sendNotFoundEndpoint(exchange, "Данный метод не реализован, используйте " +
                                            "методы указанные в задании");
                                }
                            }
                            case "POST" -> {
                                if (length == 2 && isEndpointTask(pathParts[1])) {
                                    if (isEndpointTask(pathParts[1])) {
                                        handlePostRequest(exchange);
                                    } else {
                                        sendNotFoundEndpoint(exchange, "Данный эндпоинт не реализован, используйте " +
                                                "эндпоинты указанные в задании");
                                    }
                                } else if (length == 3 && isEndpointTask(pathParts[1])) {
                                    if (isEndpointTask(pathParts[1])) {
                                        if (isInteger(pathParts[2])) {
                                            handlePostRequest(exchange);
                                        } else {
                                            sendHasData(exchange, "id должен быть числом");
                                        }
                                    } else {
                                        sendNotFoundEndpoint(exchange, "Данный эндпоинт не реализован, используйте " +
                                                "эндпоинты указанные в задании");
                                    }
                                } else {
                                    sendNotFoundEndpoint(exchange, "Данный эндпоинт не реализован, используйте " +
                                            "эндпоинты указанные в задании");
                                }
                            }
                            case "DELETE" -> {
                                if (length == 2) {
                                    if (isEndpointTask(pathParts[1])) {
                                        handleDeleteRequest(exchange);
                                    } else {
                                        sendNotFoundEndpoint(exchange, "Данный эндпоинт не реализован, используйте " +
                                                "эндпоинты указанные в задании");
                                    }
                                } else if (length == 3) {
                                    if (isEndpointTask(pathParts[1])) {
                                        if (isInteger(pathParts[2])) {
                                            handleDeleteRequest(exchange);
                                        } else {
                                            sendHasData(exchange, "id должен быть числом");
                                        }
                                    } else {
                                        sendNotFoundEndpoint(exchange, "Данный эндпоинт не реализован, используйте " +
                                                "эндпоинты указанные в задании");
                                    }
                                } else {
                                    sendNotFoundEndpoint(exchange, "Данный меод не реализован, " +
                                            "используйте методы указанные в задании");
                                }
                            }
                            default -> {
                                sendNotFoundEndpoint(exchange, "Данный эндпоинт не реализован, " +
                                        "используйте эндпоинты указанные в задании");
                            }
                        }
                    } else {
                        sendNotFoundEndpoint(exchange, "Данный эндпоинт не реализован, используйте эндпоинт указанные " +
                                "в задании");
                    }
                } catch (ManagerSaveException e) {
                    sendHasData(exchange, e.getMessage());
                } catch (ValidationException e) {
                    sendHasInteractions(exchange, e.getMessage());
                }

            } catch (Exception e) {
                sendError(exchange, e.getMessage());
            }
        } catch (Exception e) {
            sendError(exchange, e.getMessage());
        }
    }

    protected void handleGetRequest(HttpExchange httpExchange) throws IOException {

    }

    protected void handlePostRequest(HttpExchange httpExchange) throws IOException {

    }

    protected void handleDeleteRequest(HttpExchange httpExchange) throws IOException {

    }

    protected void sendText(HttpExchange exchange, String text) throws IOException {
        try (exchange) {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(200, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (Exception e) {
            sendError(exchange, e.getMessage());
            exchange.close();
        }
    }

    protected void sendTextPost(HttpExchange exchange, String text) throws IOException {
        try (exchange) {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(201, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (Exception e) {
            sendError(exchange, e.getMessage());
            exchange.close();
        }
    }

    protected void sendTextDelete(HttpExchange exchange, String text) throws IOException {
        try (exchange) {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(204, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (Exception e) {
            sendError(exchange, e.getMessage());
            exchange.close();
        }
    }

    protected void sendHasData(HttpExchange exchange, String text) throws IOException {

        try (exchange) {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(400, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (Exception e) {
            sendError(exchange, e.getMessage());
            exchange.close();
        }
    }

    protected void sendNotFoundId(HttpExchange exchange, String text) throws IOException {
        try (exchange) {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(404, resp.length);
            exchange.getResponseBody().write(resp);
        }  catch (Exception e) {
            sendError(exchange, e.getMessage());
            exchange.close();
        }
    }

    protected void sendNotFoundEndpoint(HttpExchange exchange, String text) throws IOException {
        try (exchange) {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(405, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (Exception e) {
            sendError(exchange, e.getMessage());
            exchange.close();
        }
    }

    protected void sendHasInteractions(HttpExchange exchange, String text) throws IOException {
        try (exchange) {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(406, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (Exception e) {
            sendError(exchange, e.getMessage());
            exchange.close();
        }
    }

    protected void sendError(HttpExchange exchange, String text) throws IOException {
        try (exchange) {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(500, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (Exception e) {
            sendError(exchange, e.getMessage());
            exchange.close();
        }
    }
}