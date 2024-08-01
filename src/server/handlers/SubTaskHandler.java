package server.handlers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;
import exception.ValidationException;
import server.adapters.DurationAdapter;
import server.adapters.LocalDateTimeAdapter;
import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;
import task.manager.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class SubTaskHandler extends BaseHttpHandler {

    Gson gson;

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
            Integer taskId = sub.getId();
            if (taskId == badId) {
                isTrue = true;
                break;
            }
        }
        return isTrue;
    }

    protected boolean strIsSubTasks(String target) {
        return target.equals("subtasks");
    }

    protected boolean strIsId(String target) {
        return target.matches("\\d+");
    }

    @Override
    protected void handleGetRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        if (pathParts.length == 2) {
            sendText(exchange, gson.toJson(taskManager.getAllSubtasks()));
        } else if (pathParts.length == 3) {
            Integer id = Integer.parseInt(pathParts[2]);
            if (isId(id)) {
                SubTask task = taskManager.getSubTaskId(id);
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
        SubTask task = gson.fromJson(body, SubTask.class);
        SubTask newTask;

        if (pathParts.length == 3) {
            Integer id = Integer.parseInt(pathParts[2]);
            if (id > 0) {
                if (isId(id)) {
                    if (task.getStartTime() != null && taskManager.isCheckTaskTime(task)) {
                        sendHasInteractions(exchange, "Задачи пересекаются");
                    } else if (task.getStartTime() != null && !taskManager.isCheckTaskTime(task)) {
                        newTask = new SubTask(task.getName(), task.getDescription(), task.getStateTask(),
                                task.getIdEpic(), task.getId(), task.getStartTime(), task.getDuration());
                        taskManager.updateSubTask(newTask);
                        sendTextPost(exchange, gson.toJson(newTask));
                    } else {
                        newTask = new SubTask(task.getName(), task.getDescription(), task.getStateTask(),
                                task.getIdEpic(), task.getId());
                        taskManager.updateSubTask(newTask);
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
                newTask = new SubTask(task.getName(), task.getDescription(), task.getIdEpic(), task.getStartTime(),
                        task.getDuration());
                taskManager.addSubTask(newTask);
                sendTextPost(exchange, gson.toJson(newTask));
            } else {
                newTask = new SubTask(task.getName(), task.getDescription(), task.getIdEpic());
                taskManager.addSubTask(newTask);
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
            taskManager.deleteAllSubTask();
            sendTextDelete(exchange, "Список задач очищен.");
        } else if (pathParts.length == 3) {
            Integer id = Integer.parseInt(pathParts[2]);
            if (isId(id)) {
                taskManager.deleteSubTask(id);
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