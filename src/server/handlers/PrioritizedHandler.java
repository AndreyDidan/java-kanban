package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.adapters.DurationAdapter;
import server.adapters.LocalDateTimeAdapter;
import task.manager.model.Task;
import task.manager.service.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class PrioritizedHandler extends BaseHttpHandler {

    Gson gson;

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @Override
    protected void handleGetRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        try (exchange) {
            if (pathParts.length == 2) {
                sendText(exchange, gson.toJson(taskManager.getPrioritizedTask()));
            } else {
                sendNotFoundEndpoint(exchange, "Данный меод не реализован, используйте методы указанные в задании");
            }
        } catch (Exception e) {
            sendError(exchange, e.getMessage());
        }
        /*} else {
            exchange.sendResponseHeaders(500, 0);
            exchange.getResponseBody().write("Неизвестная ошибка".getBytes());
            exchange.close();
        }*/
    }
}