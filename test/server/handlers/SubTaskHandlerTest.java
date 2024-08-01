package server.handlers;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.manager.model.Task;
import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.service.Managers;
import task.manager.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskHandlerTest {
    HttpServer httpServer;
    TaskManager tm = Managers.getDefault();

    @BeforeEach
    public void beforeEach() throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(8080), 0);
        httpServer.createContext("/epics", new TaskHandler(tm));
        httpServer.createContext("/subtasks", new TaskHandler(tm));
        httpServer.start();
    }
    @AfterEach
    public void afterEach() {
        httpServer.stop(1);
    }


    @Test
    void testHandlePostRequestAddNewTask() throws IOException,InterruptedException {

        tm.addEpic(new Epic("Task1", "Desc1"));

        String taskJson = "{ \"name\": \"Task1\", "+
                "\"description\": \"Description1\" " +
                "\"idEpic\": \"1\" " +
                "}";

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        ArrayList<SubTask> tasksFromManager = tm.getAllSubtasks();
        SubTask task1 = tm.getSubTaskId(1);

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Task1", task1.getName(), "Некорректное имя задачи");

    }

    @Test
    void testHandleGetAllTasks() throws IOException,InterruptedException {
        tm.addEpic(new Epic("Task1", "Desc1"));
        tm.addSubTask(new SubTask("Task1", "Desc1", 1));
        tm.addSubTask(new SubTask("Task2", "Desc2", 1));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
    }

    @Test
    void testHandleGetOneTask() throws IOException,InterruptedException {
        tm.addEpic(new Epic("Task1", "Desc1"));
        tm.addSubTask(new SubTask("Task1", "Desc1", 1));
        tm.addSubTask(new SubTask("Task2", "Desc2", 1));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/2"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
    }

    @Test
    void testHandleDeleteRequestDeleteTask() throws IOException,InterruptedException {

        Epic epic = new Epic("Task1", "Desc1");
        SubTask task1 = new SubTask("Task1", "Desc1", 1);
        SubTask task2 = new SubTask("Task1", "Desc1", 1);
        tm.addEpic(epic);
        tm.addSubTask(task1);
        tm.addSubTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/"+task1.getId()))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode(), "Код ответа не совпадает");
        assertFalse(tm.getAllSubtasks().contains(task1));
        assertTrue(tm.getAllSubtasks().contains(task2));
    }

    @Test
    void testHandleDeleteRequestDeleteAllTasks() throws IOException,InterruptedException {

        Epic epic = new Epic("Task1", "Desc1");
        SubTask task1 = new SubTask("Task1", "Desc1", 1);
        SubTask task2 = new SubTask("Task1", "Desc1", 1);
        tm.addEpic(epic);
        tm.addSubTask(task1);
        tm.addSubTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/"))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode(), "Код ответа не совпадает");
        assertFalse(tm.getAllSubtasks().contains(task1));
        assertFalse(tm.getAllSubtasks().contains(task2));
    }
}