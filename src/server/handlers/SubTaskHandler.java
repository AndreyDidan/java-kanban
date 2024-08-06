package server.handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import server.adapters.DurationAdapter;
import server.adapters.LocalDateTimeAdapter;
import task.manager.model.SubTask;
import task.manager.service.TaskManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SubTaskHandler extends BaseHttpHandler {

    private final Gson gson;

    public SubTaskHandler(TaskManager taskManager) {
        super(taskManager);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    private boolean isId(int badId) {
        boolean isTrue = false;
        ArrayList<SubTask> subTaska = taskManager.getAllSubtasks();

        for (SubTask sub : subTaska) {
            int taskId = sub.getId();
            if (taskId == badId) {
                isTrue = true;
                break;
            }
        }
        return isTrue;
    }

    @Override
    protected void handleGetRequest(HttpExchange exchange) throws IOException {

        try {
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            if (pathParts.length == 2 && pathParts[1].equals("subtasks")) {
                sendResponse(exchange, gson.toJson(taskManager.getAllSubtasks()), 200);
            } else if (pathParts.length == 3 && pathParts[1].equals("subtasks")) {
                int id = Integer.parseInt(pathParts[2]);
                if (isInteger(pathParts[2])) {
                    if (isId(id)) {
                        SubTask task = taskManager.getSubTaskId(id);
                        sendResponse(exchange, gson.toJson(task), 200);
                    } else {
                        sendResponse(exchange, "Такой задачи неусещствует id = " + id, 404);
                    }
                } else {
                    sendResponse(exchange, "id должен быть числом", 405);
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
        SubTask task = gson.fromJson(body, SubTask.class);
        JsonElement jsonElement = JsonParser.parseString(body);
        SubTask newTask;

        if (pathParts.length == 3 && pathParts[1].equals("subtasks")) {
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
                                newTask = new SubTask(task.getName(), task.getDescription(), task.getStateTask(),
                                        task.getIdEpic(), id, task.getStartTime(), task.getDuration());
                                taskManager.updateSubTask(newTask);
                                sendResponse(exchange, gson.toJson(newTask), 201);
                            } else {
                                newTask = new SubTask(task.getName(), task.getDescription(), task.getStateTask(),
                                        task.getIdEpic(), id);
                                taskManager.updateSubTask(newTask);
                                sendResponse(exchange, gson.toJson(newTask), 201);
                            }
                        }
                    } else {
                        sendResponse(exchange, "Такой задачи неусещствует id = " + id, 404);
                    }
                } else {
                    sendResponse(exchange, "id не может быть не числом и не может быть меньше 0.", 400);
                }
            } else {
                sendResponse(exchange, "id должен быть числом", 405);
            }
        } else if (pathParts.length == 2 && pathParts[1].equals("subtasks")) {
            if (task.getStartTime() != null && taskManager.isCheckTaskTime(task)) {
                sendResponse(exchange, "Задачи пересекаются", 406);
            } else if (task.getStartTime() != null && !taskManager.isCheckTaskTime(task)) {
                newTask = new SubTask(task.getName(), task.getDescription(), task.getIdEpic(), task.getStartTime(),
                        task.getDuration());
                taskManager.addSubTask(newTask);
                sendResponse(exchange, gson.toJson(newTask), 201);
            } else {
                newTask = new SubTask(task.getName(), task.getDescription(), task.getIdEpic());
                taskManager.addSubTask(newTask);
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

        try {
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            if (pathParts.length == 3 && pathParts[1].equals("subtasks")) {
                int id = Integer.parseInt(pathParts[2]);
                if (isInteger(pathParts[2])) {
                    if (id > 0) {
                        if (isId(id)) {
                            taskManager.deleteSubTask(id);
                            sendResponse(exchange, "Подзадача id = " + id + " удалена.", 204);
                        } else {
                            sendResponse(exchange, "Такой подзадачи неусещствует id = " + id, 404);
                        }
                    } else {
                        sendResponse(exchange, "id не может быть не числом и не может быть меньше 0.", 400);
                    }
                } else {
                    sendResponse(exchange, "id должен быть числом", 405);
                }
            } else if (pathParts.length == 2 && pathParts[1].equals("subtasks")) {
                taskManager.deleteAllSubTask();
                sendResponse(exchange, "Подзадачи удалены.", 204);
            } else {
                exchange.sendResponseHeaders(405, 0);
                exchange.getResponseBody().write("Данный метод не реализован.".getBytes());
                exchange.close();
            }
        } catch (Exception e) {
            sendResponse(exchange, e.getMessage(), 500);
        }
    }
}