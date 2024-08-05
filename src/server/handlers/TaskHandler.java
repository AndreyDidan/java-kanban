package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import server.adapters.DurationAdapter;
import server.adapters.LocalDateTimeAdapter;
import task.manager.service.TaskManager;
import task.manager.model.Task;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class TaskHandler extends BaseHttpHandler {

    private final Gson gson;

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    private boolean isId(int badId) {
        boolean isTrue = false;
        ArrayList<Task> taska = taskManager.getAllTasks();

        for (Task task : taska) {
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

            if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
                sendResponse(exchange, gson.toJson(taskManager.getAllTasks()), 200);
            } else if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
                int id = Integer.parseInt(pathParts[2]);
                if (isInteger(pathParts[2])) {
                    if (isId(id)) {
                        Task task = taskManager.getTaskId(id);
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
        Task task = gson.fromJson(body, Task.class);
        JsonElement jsonElement = JsonParser.parseString(body);
        Task newTask;

        if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
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
                                newTask = new Task(id, task.getName(), task.getDescription(), task.getStateTask(),
                                        task.getStartTime(), task.getDuration());
                                taskManager.updateTask(newTask);
                                sendResponse(exchange, gson.toJson(newTask), 201);
                            } else {
                                newTask = new Task(id, task.getName(), task.getDescription(), task.getStateTask());
                                taskManager.updateTask(newTask);
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
        } else if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
            if (!jsonElement.isJsonObject()) {
                sendResponse(exchange, "Запрос не в формае json", 400);
            } else {
                if (task.getStartTime() != null && taskManager.isCheckTaskTime(task)) {
                    sendResponse(exchange, "Задачи пересекаются", 406);
                } else if (task.getStartTime() != null && !taskManager.isCheckTaskTime(task)) {
                    newTask = new Task(task.getName(), task.getDescription(), task.getStartTime(), task.getDuration());
                    taskManager.addTask(newTask);
                    sendResponse(exchange, gson.toJson(newTask), 201);
                } else {
                    newTask = new Task(task.getName(), task.getDescription());
                    taskManager.addTask(newTask);
                    sendResponse(exchange, gson.toJson(newTask), 201);
                }
            }
        } else {
            exchange.sendResponseHeaders(405, 0);
            exchange.getResponseBody().write("Данный метод не реализован".getBytes());
            exchange.close();
        }
    }

    @Override
    protected void handleDeleteRequest(HttpExchange exchange) throws IOException {

        try {
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
                taskManager.deleteAllTasks();
                sendResponse(exchange, "Список задач очищен.", 204);
            } else if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
                int id = Integer.parseInt(pathParts[2]);
                if (isInteger(pathParts[2])) {
                    if (id > 0) {
                        if (isId(id)) {
                            taskManager.deleteTask(id);
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
                exchange.getResponseBody().write("Данный метод не реализован.".getBytes());
                exchange.close();
            }
        } catch (Exception e) {
            sendResponse(exchange, "Неизвестная ошибка", 500);
        }
    }
}