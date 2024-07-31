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


        if (pathParts.length == 2 && strIsSubTasks(pathParts[1])) {
            ArrayList<SubTask> subTaskList = this.taskManager.getAllSubtasks();
            sendText(exchange, gson.toJson(subTaskList));
        } else if (pathParts.length == 3 && strIsSubTasks(pathParts[1]) && strIsId(pathParts[2])) {
            int id = Integer.parseInt(pathParts[2]);
            sendText(exchange, String.valueOf(this.taskManager.getSubTaskId(id)));
        } else {
            sendNotFoundEndpoint(exchange, "405");
        }



        /*String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        if (pathParts.length == 2) {
            sendText(exchange, gson.toJson(taskManager.getAllSubtasks()));
        } else if (pathParts.length == 3) {
            Integer id = Integer.parseInt(pathParts[2]);
            if (isId(id)) {
                SubTask subTask = taskManager.getSubTaskId(id);
                sendText(exchange, gson.toJson(subTask));
            } else {
                sendNotFoundId(exchange, "Такой задачи неусещствует id = " + id);
            }
        } else {
            exchange.sendResponseHeaders(500, 0);
            exchange.getResponseBody().write("Неизвестная ошибка".getBytes());
            exchange.close();
        }*/
    }

    protected boolean strIsEpics(String target) {
        return target.equals("epics");
    }

    @Override
    protected void handlePostRequest(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        if (pathParts.length == 2 && strIsEpics(pathParts[1])) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            JsonElement jsonElement = JsonParser.parseString(body);

            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                JsonElement id = jsonObject.get("id");
                JsonElement name = jsonObject.get("name");
                JsonElement description = jsonObject.get("description");

                if (name == null || description == null) {
                    sendHasData(exchange,"Не заполнены обязательные поля");
                } else {
                    String nameValue = name.getAsString();
                    String descriptionValue = description.getAsString();

                    Epic epic = new Epic(nameValue, descriptionValue);

                    if (id != null && id.getAsInt() != 0) {
                        epic.setId(id.getAsInt());
                        this.taskManager.updateEpic(epic);
                    } else {
                        this.taskManager.addEpic(epic);
                    }
                    sendTextPost(exchange, "может");
                }
            } else {
                sendHasData(exchange, "Тело запроса не в формате json");
            }
        } else {
            sendNotFoundEndpoint(exchange, "нет эндп");
        }







        /*String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        SubTask subTask = gson.fromJson(body, SubTask.class);
        SubTask newSubTask;

        if (pathParts.length == 3) {
            Integer id = Integer.parseInt(pathParts[2]);
            if (id > 0) {
                if (isId(id)) {
                    if (subTask.getStartTime() != null && taskManager.isCheckTaskTime(subTask)) {
                        sendHasInteractions(exchange, "Подзадачи пересекаются");
                    } else if (subTask.getStartTime() != null && !taskManager.isCheckTaskTime(subTask)) {
                        newSubTask = new SubTask(subTask.getName(), subTask.getDescription(), subTask.getStateTask(),
                                subTask.getIdEpic(), id, subTask.getStartTime(), subTask.getDuration());
                        taskManager.updateSubTask(newSubTask);
                        sendTextPost(exchange, gson.toJson(newSubTask));
                    } else {
                        newSubTask = new SubTask(subTask.getName(), subTask.getDescription(), subTask.getStateTask(),
                                subTask.getIdEpic(), id);
                        taskManager.updateSubTask(newSubTask);
                        sendTextPost(exchange, gson.toJson(newSubTask));
                    }
                } else {
                    sendNotFoundId(exchange, "Такой подзадачи неусещствует id = " + id);
                }
            }
        } else if (pathParts.length == 2) {
            if (subTask.getStartTime() != null && taskManager.isCheckTaskTime(subTask)) {
                sendHasInteractions(exchange, "Подзадачи пересекаются");
            } else if (subTask.getStartTime() != null && !taskManager.isCheckTaskTime(subTask)) {
                newSubTask = new SubTask(subTask.getName(), subTask.getDescription(), subTask.getIdEpic(),
                        subTask.getStartTime(), subTask.getDuration());
                taskManager.addSubTask(newSubTask);
                sendTextPost(exchange, gson.toJson(newSubTask));
            } else {
                newSubTask = new SubTask(subTask.getName(), subTask.getDescription(), subTask.getIdEpic());
                taskManager.addSubTask(newSubTask);
                sendTextPost(exchange, gson.toJson(newSubTask));
            }
        } else {
            exchange.sendResponseHeaders(500, 0);
            exchange.getResponseBody().write("Неизвестная ошибка".getBytes());
            exchange.close();
        }
    }

    @Override
    protected void handleDeleteRequest(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        if (pathParts.length == 2) {
            taskManager.deleteAllSubTask();
            sendTextDelete(exchange, "Список подзадач очищен.");
        } else if (pathParts.length == 3) {
            Integer id = Integer.parseInt(pathParts[2]);
            if (isId(id)) {
                taskManager.deleteSubTask(id);
                sendTextDelete(exchange, "Подзадача id = " + id + " удалена.");
            } else {
                sendNotFoundId(exchange, "Такой подзадачи нет id = " + id);
            }
        } else {
            exchange.sendResponseHeaders(500, 0);
            exchange.getResponseBody().write("Internal Server Error".getBytes());
            exchange.close();
        }*/
    }
}