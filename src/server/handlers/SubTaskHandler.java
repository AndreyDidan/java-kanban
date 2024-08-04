package server.handlers;

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

            if (pathParts.length == 2) {
                sendText(exchange, gson.toJson(taskManager.getAllSubtasks()));
            } else if (pathParts.length == 3) {
                int id = Integer.parseInt(pathParts[2]);
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
        } catch (Exception e) {
            sendError(exchange, e.getMessage());
        }
    }

    @Override
    protected void handlePostRequest(HttpExchange exchange) throws IOException {

        try {
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            SubTask task = gson.fromJson(body, SubTask.class);
            SubTask newTask;

            if (pathParts.length == 3) {
                int id = Integer.parseInt(pathParts[2]);
                if (id > 0) {
                    if (isId(id)) {
                        if (task.getStartTime() != null && taskManager.isCheckTaskTime(task)) {
                            sendHasInteractions(exchange, "Задачи пересекаются");
                        } else if (task.getStartTime() != null && !taskManager.isCheckTaskTime(task)) {
                            newTask = new SubTask(task.getName(), task.getDescription(), task.getStateTask(),
                                    task.getIdEpic(), id, task.getStartTime(), task.getDuration());
                            taskManager.updateSubTask(newTask);
                            sendTextPost(exchange, gson.toJson(newTask));
                        } else {
                            newTask = new SubTask(task.getName(), task.getDescription(), task.getStateTask(),
                                    task.getIdEpic(), id);
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
        } catch (Exception e) {
            sendError(exchange, e.getMessage());
        }
    }

    @Override
    protected void handleDeleteRequest(HttpExchange exchange) throws IOException {

        try {
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            if (pathParts.length == 3) {
                int id = Integer.parseInt(pathParts[2]);
                if (id > 0) {
                    if (isId(id)) {
                        taskManager.deleteSubTask(id);
                        sendTextDelete(exchange, "Подзадача id = " + id + " удалена.");
                    } else {
                        sendNotFoundId(exchange, "Такой подзадачи неусещствует id = " + id);
                    }
                } else {
                    sendHasData(exchange, "id не может быть не числом и не может быть меньше 0.");
                }
            } else if (pathParts.length == 2) {
                taskManager.deleteAllSubTask();
                sendTextDelete(exchange, "Подзадачи удалены.");
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