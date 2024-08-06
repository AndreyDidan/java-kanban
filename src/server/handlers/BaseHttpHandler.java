package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.ManagerSaveException;
import exception.NotFoundException;
import exception.ValidationException;
import task.manager.service.TaskManager;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    protected final TaskManager taskManager;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected boolean isEndpointGET(String str) {
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

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        try {
            switch (method) {
                case "GET":
                    if (isEndpointGET(pathParts[1])) {
                        handleGetRequest(exchange);
                        break;
                    }
                case "POST":
                    if (isEndpointTask(pathParts[1])) {
                        handlePostRequest(exchange);
                        break;
                    }
                case "DELETE":
                    if (isEndpointTask(pathParts[1])) {
                        handleDeleteRequest(exchange);
                        break;
                    }
                default:
                    sendResponse(exchange, "Данный метод не реализован.", 405);
            }
        } catch (ManagerSaveException e) {
            sendResponse(exchange, e.getMessage(), 400);
        } catch (ValidationException e) {
            sendResponse(exchange, e.getMessage(), 406);
        } catch (NotFoundException e) {
            sendResponse(exchange, e.getMessage(), 404);
        } catch (Exception e) {
            sendResponse(exchange, e.getMessage(), 500);
        }
    }

    protected abstract void handleGetRequest(HttpExchange httpExchange) throws IOException;

    protected abstract void handlePostRequest(HttpExchange httpExchange) throws IOException;

    protected abstract void handleDeleteRequest(HttpExchange httpExchange) throws IOException;

    protected void sendResponse(HttpExchange exchange, String text, int codeResponse) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(codeResponse, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }
}