package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import server.adapters.DurationAdapter;
import server.adapters.LocalDateTimeAdapter;
import task.manager.service.TaskManager;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class HistoryHandler extends BaseHttpHandler {

    private final Gson gson;

    public HistoryHandler(TaskManager taskManager) {
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
            if (pathParts.length == 2 && pathParts[1].equals("history")) {
                sendResponse(exchange, gson.toJson(taskManager.getHistory()), 200);
            } else {
                sendResponse(exchange, "Данный меод не реализован, используйте методы указанные в задании",
                        405);
            }
        } catch (Exception e) {
            sendResponse(exchange, e.getMessage(), 500);
        }
    }

    @Override
    protected void handlePostRequest(HttpExchange httpExchange) throws IOException {

    }

    @Override
    protected void handleDeleteRequest(HttpExchange httpExchange) throws IOException {

    }
}