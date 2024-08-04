package server.handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import server.HttpTaskServer;
import task.manager.model.Epic;
import task.manager.model.StateTask;
import task.manager.model.SubTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import task.manager.service.Managers;
import task.manager.service.TaskManager;
import java.util.List;

public class EpicHandlerTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = Managers.getDefault();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public EpicHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks();
        manager.deleteAllSubTask();
        manager.deleteAllEpics();
        HttpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        HttpTaskServer.stop(0);
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic task = new Epic("Epic 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        ArrayList<Epic> tasksFromManager = manager.getAllEpics() ;

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Epic 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic task = new Epic("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        manager.addEpic(task);
        Epic task1 = new Epic(1,"Test 3", "Testing task 4", StateTask.NEW);
        // конвертируем её в JSON
        String taskJson = gson.toJson(task1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        ArrayList<Epic> tasksFromManager = manager.getAllEpics() ;

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 3", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    void testGetSubTasksInEpic() throws IOException, InterruptedException {
        Epic task = new Epic("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        SubTask task1 = new SubTask("Test 3", "Testing task 3", 1,
                LocalDateTime.now().plus(Duration.ofHours(6)), Duration.ofMinutes(5));
        SubTask task2 = new SubTask("Test 4", "Testing task 4", 1,
                LocalDateTime.now().plus(Duration.ofHours(7)), Duration.ofMinutes(5));
        manager.addEpic(task);
        manager.addSubTask(task1);
        manager.addSubTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<SubTask> subtasksFromEpic = gson.fromJson(response.body(), new TypeToken<List<SubTask>>() {}.getType());
        assertEquals(200, response.statusCode());
        assertEquals(2, subtasksFromEpic.size(), "Некорректное количество подзадач в эпике");
    }

    @Test
    public void testGetAllEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic task = new Epic("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        Epic task1 = new Epic("Test 3", "Testing task 3", LocalDateTime.now().plus(Duration.ofHours(6)),
                Duration.ofMinutes(5));
        manager.addEpic(task);
        manager.addEpic(task1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        ArrayList<Epic> tasksFromManager = manager.getAllEpics() ;
        List<Epic> tasks = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {}.getType());

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
        assertNotNull(tasks, "Задачи не возвращаются");
    }

    @Test
    public void testGetOneEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic task = new Epic("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        Epic task1 = new Epic("Test 3", "Testing task 3", LocalDateTime.now().plus(Duration.ofHours(6)),
                Duration.ofMinutes(5));
        manager.addEpic(task);
        manager.addEpic(task1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        ArrayList<Epic> tasksFromManager = manager.getAllEpics() ;
        Epic returnedTask = gson.fromJson(response.body(), Epic.class);

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 3", tasksFromManager.get(1).getName(), "Некорректное имя задачи");
        assertNotNull(returnedTask, "Задачи не возвращаются");
        assertEquals(task1, returnedTask, "Некорректное количество задач");
    }

    @Test
    public void testDeleteAllEpics() throws IOException, InterruptedException {
        // создаём задачу
        Epic task = new Epic("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        Epic task1 = new Epic("Test 3", "Testing task 3", LocalDateTime.now().plus(Duration.ofHours(6)),
                Duration.ofMinutes(5));
        manager.addEpic(task);
        manager.addEpic(task1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(204, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        ArrayList<Epic> tasksFromManager = manager.getAllEpics() ;

        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteOneEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic task = new Epic("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        Epic task1 = new Epic("Test 3", "Testing task 3", LocalDateTime.now().plus(Duration.ofHours(6)),
                Duration.ofMinutes(5));
        manager.addEpic(task);
        manager.addEpic(task1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(204, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        ArrayList<Epic> tasksFromManager = manager.getAllEpics() ;
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }
}