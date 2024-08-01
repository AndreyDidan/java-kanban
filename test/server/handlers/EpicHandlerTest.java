package server.handlers;

import task.manager.model.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import task.manager.model.Epic;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import com.sun.net.httpserver.HttpServer;
import task.manager.service.Managers;
import task.manager.service.TaskManager;
import java.net.InetSocketAddress;
import static org.junit.jupiter.api.Assertions.*;

class EpicHandlerTest {

    HttpServer httpServer;
    TaskManager tm = Managers.getDefault();

    @BeforeEach
    public void beforeEach() throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(8080), 0);
        httpServer.createContext("/epics", new TaskHandler(tm));
        httpServer.start();
    }
    @AfterEach
    public void afterEach() {
        httpServer.stop(1);
    }


    @Test
    void testHandlePostRequestAddNewEpic() throws IOException,InterruptedException {

        String taskJson = "{ \"name\": \"Task1\", "+
                "\"description\": \"Description1\" " +
                ", \"duration\": \"12\" " +
                ", \"startTime\": \"15.03.2024 14:50\"" +
                "}";

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        ArrayList<Epic> tasksFromManager = tm.getAllEpics();
        Epic task1 = tm.getEpicId(1);

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Task1", task1.getName(), "Некорректное имя задачи");

    }

    @Test
    void testHandleGetAllEpics() throws IOException,InterruptedException {
        tm.addEpic(new Epic("Task1", "Desc1"));
        tm.addEpic(new Epic("Task2", "Desc2"));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
    }

    @Test
    void testHandleGetOneEpic() throws IOException,InterruptedException {
        tm.addEpic(new Epic("Task1", "Desc1"));
        tm.addEpic(new Epic("Task2", "Desc2"));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/1"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
    }


    @Test
    void testHandleDeleteRequestDeleteEpic() throws IOException,InterruptedException {

        Epic task1 = new Epic("Task1", "Desc1");
        Epic task2 = new Epic("Task1", "Desc1");
        tm.addTask(task1);
        tm.addTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/"+task1.getId()))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode(), "Код ответа не совпадает");
        assertFalse(tm.getAllEpics().contains(task1));
        assertTrue(tm.getAllEpics().contains(task2));
    }

    @Test
    void testHandleDeleteRequestDeleteAllEpics() throws IOException,InterruptedException {

        Epic task1 = new Epic("Task1", "Desc1");
        Epic task2 = new Epic("Task1", "Desc1");
        tm.addTask(task1);
        tm.addTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/"))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode(), "Код ответа не совпадает");
        assertFalse(tm.getAllEpics().contains(task1));
        assertFalse(tm.getAllEpics().contains(task2));
    }

}