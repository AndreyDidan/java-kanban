package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

    Gson gson;

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
            Integer taskId = task.getId();
            if (taskId == badId) {
                isTrue = true;
                break;
            }
        }
        return isTrue;
    }

    @Override
    protected void handleGetRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        if (pathParts.length == 2) {
            sendText(exchange, gson.toJson(taskManager.getAllTasks()));
        } else if (pathParts.length == 3) {
            Integer id = Integer.parseInt(pathParts[2]);
            if (isId(id)) {
                Task task = taskManager.getTaskId(id);
                sendText(exchange, gson.toJson(task));
            } else {
                sendNotFoundId(exchange, "Такой задачи неусещствует id = " + id);
            }
        } else {
            exchange.sendResponseHeaders(500, 0);
            exchange.getResponseBody().write("Неизвестная ошибка".getBytes());
            exchange.close();
        }
    }

    @Override
    protected void handlePostRequest(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, Task.class);
        Task newTask;

        if (pathParts.length == 3) {
            Integer id = Integer.parseInt(pathParts[2]);
            if (id > 0) {
                if (isId(id)) {
                    if (task.getStartTime() != null && taskManager.isCheckTaskTime(task)) {
                        sendHasInteractions(exchange, "Задачи пересекаются");
                    } else if (task.getStartTime() != null && !taskManager.isCheckTaskTime(task)) {
                        newTask = new Task(id, task.getName(), task.getDescription(), task.getStateTask(),
                                task.getStartTime(), task.getDuration());
                        taskManager.updateTask(newTask);
                        sendTextPost(exchange, gson.toJson(newTask));
                    } else {
                        newTask = new Task(id, task.getName(), task.getDescription(), task.getStateTask());
                        taskManager.updateTask(newTask);
                        sendTextPost(exchange, gson.toJson(newTask));
                    }
                } else {
                    sendNotFoundId(exchange, "Такой задачи неусещствует id = " + id);
                }
            }
        } else if (pathParts.length == 2) {
            if (task.getStartTime() != null && taskManager.isCheckTaskTime(task)) {
                sendHasInteractions(exchange, "Задачи пересекаются");
            } else if (task.getStartTime() != null && !taskManager.isCheckTaskTime(task)) {
                newTask = new Task(task.getName(), task.getDescription(), task.getStartTime(), task.getDuration());
                taskManager.addTask(newTask);
                sendTextPost(exchange, gson.toJson(newTask));
            } else {
                newTask = new Task(task.getName(), task.getDescription());
                taskManager.addTask(newTask);
                sendTextPost(exchange, gson.toJson(newTask));
            }
        } else {
            exchange.sendResponseHeaders(500, 0);
            exchange.getResponseBody().write("Internal Server Error".getBytes());
            exchange.close();
        }
    }

    @Override
    protected void handleDeleteRequest(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        if (pathParts.length == 2) {
            taskManager.deleteAllTasks();
            sendTextDelete(exchange, "Список задач очищен.");
        } else if (pathParts.length == 3) {
            Integer id = Integer.parseInt(pathParts[2]);
            if (isId(id)) {
                taskManager.deleteTask(id);
                sendTextDelete(exchange, "Задача id = " + id + " удалена.");
            } else {
                sendNotFoundId(exchange, "Такой задачи нет id = " + id);
            }
        } else {
            exchange.sendResponseHeaders(500, 0);
            exchange.getResponseBody().write("Internal Server Error".getBytes());
            exchange.close();
        }
    }
}