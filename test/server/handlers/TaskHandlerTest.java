package server.handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import server.HttpTaskServer;
import task.manager.model.Task;
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

public class TaskHandlerTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = Managers.getDefault();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public TaskHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks();
        manager.deleteAllSubTask();
        manager.deleteAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop(0);
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        ArrayList<Task> tasksFromManager = manager.getAllTasks() ;

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        manager.addTask(task);
        Task task1 = new Task("Test 3", "Testing task 4");
        // конвертируем её в JSON
        String taskJson = gson.toJson(task1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        ArrayList<Task> tasksFromManager = manager.getAllTasks() ;

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 3", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        Task task1 = new Task("Test 3", "Testing task 3", LocalDateTime.now().plus(Duration.ofHours(6)),
                Duration.ofMinutes(5));
        manager.addTask(task);
        manager.addTask(task1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        ArrayList<Task> tasksFromManager = manager.getAllTasks() ;
        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {}.getType());

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
        assertNotNull(tasks, "Задачи не возвращаются");
    }

    @Test
    public void testGetOneTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        Task task1 = new Task("Test 3", "Testing task 3", LocalDateTime.now().plus(Duration.ofHours(6)),
                Duration.ofMinutes(5));
        manager.addTask(task);
        manager.addTask(task1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        ArrayList<Task> tasksFromManager = manager.getAllTasks() ;
        Task returnedTask = gson.fromJson(response.body(), Task.class);

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 3", tasksFromManager.get(1).getName(), "Некорректное имя задачи");
        assertNotNull(returnedTask, "Задачи не возвращаются");
        assertEquals(task1, returnedTask, "Некорректное количество задач");
    }

    @Test
    public void testDeleteAllTasks() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        Task task1 = new Task("Test 3", "Testing task 3", LocalDateTime.now().plus(Duration.ofHours(6)),
                Duration.ofMinutes(5));
        manager.addTask(task);
        manager.addTask(task1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(204, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        ArrayList<Task> tasksFromManager = manager.getAllTasks() ;

        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteOneTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        Task task1 = new Task("Test 3", "Testing task 3", LocalDateTime.now().plus(Duration.ofHours(6)),
                Duration.ofMinutes(5));
        manager.addTask(task);
        manager.addTask(task1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(204, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        ArrayList<Task> tasksFromManager = manager.getAllTasks() ;
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }
}