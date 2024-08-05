package server.handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import server.adapters.DurationAdapter;
import server.adapters.LocalDateTimeAdapter;
import task.manager.service.TaskManager;
import task.manager.model.Epic;
import com.sun.net.httpserver.HttpExchange;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStream;

public class EpicHandler extends BaseHttpHandler {

    Gson gson;

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    private boolean isId(int badId) {
        boolean isTrue = false;
        ArrayList<Epic> taska = taskManager.getAllEpics();

        for (Epic task : taska) {
            int taskId = task.getId();
            if (taskId == badId) {
                isTrue = true;
                break;
            }
        }
        return isTrue;
    }

    @Override
    protected void handleGetRequest(HttpExchange exchange) throws IOException {

        try (exchange) {
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            if (pathParts.length == 2) {
                sendText(exchange, gson.toJson(taskManager.getAllEpics()));
            } else if (pathParts.length == 3) {
                int id = Integer.parseInt(pathParts[2]);
                if (isId(id)) {
                    Epic task = taskManager.getEpicId(id);
                    sendText(exchange, gson.toJson(task));
                } else {
                    sendNotFoundId(exchange, "Такой задачи неусещствует id = " + id);
                }
            } else if (pathParts.length == 4) {

                int id = Integer.parseInt(pathParts[2]);
                if (isId(id)) {
                    sendText(exchange, gson.toJson(taskManager.getSubTasksInEpic(id)));
                }
            } else {
                exchange.sendResponseHeaders(500, 0);
                exchange.getResponseBody().write("Неизвестная ошибка".getBytes());
                exchange.close();
            }
        } catch (Exception e) {
            sendError(exchange, e.getMessage());
        }
    }

    @Override
    protected void handlePostRequest(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Epic task = gson.fromJson(body, Epic.class);
        JsonElement jsonElement = JsonParser.parseString(body);
        Epic newTask;

        if (pathParts.length == 3) {
            int id = Integer.parseInt(pathParts[2]);
            if (id > 0) {
                if (isId(id)) {
                    if (!jsonElement.isJsonObject()) {
                        sendHasData(exchange, "Запрос не в формае json");
                    } else {
                        if (task.getStartTime() != null && taskManager.isCheckTaskTime(task)) {
                            sendHasInteractions(exchange, "Задачи пересекаются");
                        } else if (task.getStartTime() != null && !taskManager.isCheckTaskTime(task)) {
                            newTask = new Epic(id, task.getName(), task.getDescription(), task.getStateTask(),
                                    task.getStartTime(), task.getDuration());
                            taskManager.updateEpic(newTask);
                            sendTextPost(exchange, gson.toJson(newTask));
                        } else {
                            newTask = new Epic(id, task.getName(), task.getDescription(), task.getStateTask());
                            taskManager.updateEpic(newTask);
                            sendTextPost(exchange, gson.toJson(newTask));
                        }
                    }
                } else {
                    sendNotFoundId(exchange, "Такой задачи неусещствует1 id = " + id);
                }
            } else {
                sendHasData(exchange, "id не может быть не числом и не может быть меньше 0.");
            }
        } else if (pathParts.length == 2) {
            if (task.getStartTime() != null && taskManager.isCheckTaskTime(task)) {
                sendHasInteractions(exchange, "Задачи пересекаются");
            } else if (task.getStartTime() != null && !taskManager.isCheckTaskTime(task)) {
                newTask = new Epic(task.getName(), task.getDescription(), task.getStartTime(), task.getDuration());
                taskManager.addEpic(newTask);
                sendTextPost(exchange, gson.toJson(newTask));
            } else {
                newTask = new Epic(task.getName(), task.getDescription());
                taskManager.addEpic(newTask);
                sendTextPost(exchange, gson.toJson(newTask));
            }
        } else {
            exchange.sendResponseHeaders(500, 0);
            exchange.getResponseBody().write("Неизвестная ошибка".getBytes());
            exchange.close();
        }
    }

    @Override
    protected void handleDeleteRequest(HttpExchange exchange) throws IOException {

        try (exchange) {
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            if (pathParts.length == 2) {
                taskManager.deleteAllEpics();
                sendTextDelete(exchange, "Список задач очищен.");
            } else if (pathParts.length == 3) {
                int id = Integer.parseInt(pathParts[2]);
                if (id > 0) {
                    if (isId(id)) {
                        taskManager.deleteEpic(id);
                        sendTextDelete(exchange, "Задача id = " + id + " удалена.");
                    } else {
                        sendNotFoundId(exchange, "Такой задачи нет id = " + id);
                    }
                } else {
                    sendHasData(exchange, "id не может быть не числом и не может быть меньше 0.");
                }
            } else {
                exchange.sendResponseHeaders(500, 0);
                exchange.getResponseBody().write("Неизвестная ошибка".getBytes());
                exchange.close();
            }
        } catch (Exception e) {
            sendError(exchange, e.getMessage());
        }
    }
}