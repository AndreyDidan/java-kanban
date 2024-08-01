package server.handlers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import exception.ManagerSaveException;
import server.adapters.DurationAdapter;
import server.adapters.LocalDateTimeAdapter;
import task.manager.model.*;
import task.manager.service.Converter;
import task.manager.service.TaskManager;
import task.manager.model.Task;
import task.manager.model.Epic;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import com.google.gson.JsonObject;

import static java.nio.charset.StandardCharsets.UTF_8;

class UserListTypeToken extends TypeToken<Epic> {
    // здесь ничего не нужно реализовывать
}

public class EpicHandler extends BaseHttpHandler {

    Gson gson;

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
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
            sendText(exchange, gson.toJson(taskManager.getAllEpics()));
        } else if (pathParts.length == 3) {
            Integer id = Integer.parseInt(pathParts[2]);
            if (isId(id)) {
                Epic task = taskManager.getEpicId(id);
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
        //Epic task = gson.fromJson(body, Epic.class);
        Epic newTask;

        Epic task = gson.fromJson(body, new UserListTypeToken().getType());
        Integer id = task.getId();
        String name = task.getName();
        String description = task.getDescription();
        LocalDateTime startTime = task.getStartTime();
        Duration duration = task.getDuration();

        Epic epic1 = new Epic(name, description, startTime, duration);
        Epic epic2 = new Epic(name, description);


        if (pathParts.length == 3) {
            //Integer id = Integer.parseInt(pathParts[2]);
            if (id > 0) {
                if (isId(id)) {
                    if (startTime != null && taskManager.isCheckTaskTime(epic1)) {
                        sendHasInteractions(exchange, "Задачи пересекаются");
                    } else if (startTime != null && !taskManager.isCheckTaskTime(epic1)) {
                        taskManager.updateEpic(epic1);
                        sendTextPost(exchange, gson.toJson(epic1));
                    } else {
                        //newTask = new Epic(task.getName(), task.getDescription());
                        taskManager.updateEpic(epic2);
                        sendTextPost(exchange, gson.toJson(epic2));
                    }
                } else {
                    sendNotFoundId(exchange, "Такой задачи неусещствует id = " + id);
                }
            }
        } else if (pathParts.length == 2) {
            if (task.getStartTime() != null && taskManager.isCheckTaskTime(task)) {
                sendHasInteractions(exchange, "Задачи пересекаются");
            } else if (startTime != null && !taskManager.isCheckTaskTime(epic1)) {
                //newTask = new Epic(task.getName(), task.getDescription(), task.getStartTime(), task.getDuration());
                taskManager.addEpic(epic1);
                sendTextPost(exchange, gson.toJson(epic1));
            } else {
                //newTask = new Epic(task.getName(), task.getDescription());
                taskManager.addEpic(epic2);
                sendTextPost(exchange, gson.toJson(epic2));
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
            taskManager.deleteAllEpics();
            sendTextDelete(exchange, "Список задач очищен.");
        } else if (pathParts.length == 3) {
            Integer id = Integer.parseInt(pathParts[2]);
            if (isId(id)) {
                taskManager.deleteEpic(id);
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