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

    private final Gson gson;

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

            if (pathParts.length == 2 && pathParts[1].equals("epics")) {
                sendResponse(exchange, gson.toJson(taskManager.getAllEpics()), 200);
            } else if (pathParts.length == 3 && pathParts[1].equals("epics")) {
                int id = Integer.parseInt(pathParts[2]);
                if (isInteger(pathParts[2])) {
                    if (isId(id)) {
                        Epic task = taskManager.getEpicId(id);
                        sendResponse(exchange, gson.toJson(task), 200);
                    } else {
                        sendResponse(exchange, "Такой задачи неусещствует id = " + id, 404);
                    }
                } else {
                    sendResponse(exchange, "id должен быть числом", 405);
                }
            } else if (pathParts.length == 4 && pathParts[1].equals("epics") && pathParts[3].equals("subtasks")) {
                int id = Integer.parseInt(pathParts[2]);
                if (isInteger(pathParts[2])) {
                    if (isId(id)) {
                        sendResponse(exchange, gson.toJson(taskManager.getSubTasksInEpic(id)), 200);
                    }
                } else {
                    sendResponse(exchange, "Такого эпика неусещствует id = " + id, 404);
                }
            } else {
                exchange.sendResponseHeaders(405, 0);
                exchange.getResponseBody().write("Данный метод не реализован.".getBytes());
                exchange.close();
            }
        } catch (Exception e) {
            sendResponse(exchange, e.getMessage(), 500);
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

        if (pathParts.length == 3 && pathParts[1].equals("epics")) {
            int id = Integer.parseInt(pathParts[2]);
            if (isInteger(pathParts[2])) {
                if (id > 0) {
                    if (isId(id)) {
                        if (!jsonElement.isJsonObject()) {
                            sendResponse(exchange, "Запрос не в формае json", 400);
                        } else {
                            if (task.getStartTime() != null && taskManager.isCheckTaskTime(task)) {
                                sendResponse(exchange, "Задачи пересекаются", 406);
                            } else if (task.getStartTime() != null && !taskManager.isCheckTaskTime(task)) {
                                newTask = new Epic(id, task.getName(), task.getDescription(), task.getStateTask(),
                                        task.getStartTime(), task.getDuration());
                                taskManager.updateEpic(newTask);
                                sendResponse(exchange, gson.toJson(newTask), 201);
                            } else {
                                newTask = new Epic(id, task.getName(), task.getDescription(), task.getStateTask());
                                taskManager.updateEpic(newTask);
                                sendResponse(exchange, gson.toJson(newTask), 201);
                            }
                        }
                    } else {
                        sendResponse(exchange, "Такой задачи неусещствует1 id = " + id, 404);
                    }
                } else {
                    sendResponse(exchange, "id не может быть не числом и не может быть меньше 0.", 400);
                }
            } else {
                sendResponse(exchange, "id должен быть числом", 405);
            }
        } else if (pathParts.length == 2 && pathParts[1].equals("epics")) {
            if (task.getStartTime() != null && taskManager.isCheckTaskTime(task)) {
                sendResponse(exchange, "Задачи пересекаются", 406);
            } else if (task.getStartTime() != null && !taskManager.isCheckTaskTime(task)) {
                newTask = new Epic(task.getName(), task.getDescription(), task.getStartTime(), task.getDuration());
                taskManager.addEpic(newTask);
                sendResponse(exchange, gson.toJson(newTask), 201);
            } else {
                newTask = new Epic(task.getName(), task.getDescription());
                taskManager.addEpic(newTask);
                sendResponse(exchange, gson.toJson(newTask), 201);
            }
        } else {
            exchange.sendResponseHeaders(405, 0);
            exchange.getResponseBody().write("Данный метод не реализован.".getBytes());
            exchange.close();
        }
    }

    @Override
    protected void handleDeleteRequest(HttpExchange exchange) throws IOException {

        try (exchange) {
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            if (pathParts.length == 2 && pathParts[1].equals("epics")) {
                taskManager.deleteAllEpics();
                sendResponse(exchange, "Список задач очищен.", 204);
            } else if (pathParts.length == 3 && pathParts[1].equals("epics")) {
                int id = Integer.parseInt(pathParts[2]);
                if (isInteger(pathParts[2])) {
                    if (id > 0) {
                        if (isId(id)) {
                            taskManager.deleteEpic(id);
                            sendResponse(exchange, "Задача id = " + id + " удалена.", 204);
                        } else {
                            sendResponse(exchange, "Такой задачи нет id = " + id, 404);
                        }
                    } else {
                        sendResponse(exchange, "id не может быть не числом и не может быть меньше 0.", 400);
                    }
                } else {
                    sendResponse(exchange, "id должен быть числом", 405);
                }
            } else {
                exchange.sendResponseHeaders(405, 0);
                exchange.getResponseBody().write("Такого метода не существует.".getBytes());
                exchange.close();
            }
        } catch (Exception e) {
            sendResponse(exchange, e.getMessage(), 500);
        }
    }
}