package server.handlers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.service.Managers;
import task.manager.service.TaskManager;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import server.HttpTaskServer;
import task.manager.model.StateTask;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.List;

public class SubTaskHandlerTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = Managers.getDefault();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public SubTaskHandlerTest() throws IOException {
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
    public void testAddSubTask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Epic 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        manager.addEpic(epic);
        SubTask subTask = new SubTask("SubTask1", "Test SubTask", 1, LocalDateTime.now(),
                Duration.ofMinutes(5));
        // конвертируем её в JSON
        String taskJson = gson.toJson(subTask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        ArrayList<SubTask> tasksFromManager = manager.getAllSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("SubTask1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateSubTask() throws IOException, InterruptedException {
        // создаём задачу
        Epic task = new Epic("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        manager.addEpic(task);
        SubTask task1 = new SubTask("Test 3", "Testing task 3", 1);
        manager.addSubTask(task1);
        SubTask task2 = new SubTask("Test 4", "Testing task 4", 1, LocalDateTime.now(),
                Duration.ofMinutes(5));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        ArrayList<SubTask> tasksFromManager = manager.getAllSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 4", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetAllSubTasks() throws IOException, InterruptedException {
        // создаём задачу
        Epic task = new Epic("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        SubTask task1 = new SubTask("Test 3", "Testing task 3", 1,
                LocalDateTime.now().plus(Duration.ofHours(6)), Duration.ofMinutes(5));
        SubTask task2 = new SubTask("Test 4", "Testing task 4", 1,
                LocalDateTime.now().plus(Duration.ofHours(7)), Duration.ofMinutes(5));
        manager.addEpic(task);
        manager.addSubTask(task1);
        manager.addSubTask(task2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        ArrayList<SubTask> tasksFromManager = manager.getAllSubtasks();
        List<SubTask> tasks = gson.fromJson(response.body(), new TypeToken<List<SubTask>>() {
        }.getType());

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 3", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
        assertNotNull(tasks, "Задачи не возвращаются");
    }

    @Test
    public void testGetOneSubTask() throws IOException, InterruptedException {
        // создаём задачу
        Epic task = new Epic("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        SubTask task1 = new SubTask("Test 3", "Testing task 3", 1,
                LocalDateTime.now().plus(Duration.ofHours(6)), Duration.ofMinutes(5));
        SubTask task2 = new SubTask("Test 4", "Testing task 4", 1,
                LocalDateTime.now().plus(Duration.ofHours(7)), Duration.ofMinutes(5));
        manager.addEpic(task);
        manager.addSubTask(task1);
        manager.addSubTask(task2);


        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        ArrayList<SubTask> tasksFromManager = manager.getAllSubtasks();
        SubTask returnedTask = gson.fromJson(response.body(), SubTask.class);

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 4", tasksFromManager.get(1).getName(), "Некорректное имя задачи");
        assertNotNull(returnedTask, "Задачи не возвращаются");
        assertEquals(task2, returnedTask, "Некорректное количество задач");
    }

    @Test
    public void testDeleteAllSubTasks() throws IOException, InterruptedException {
        // создаём задачу
        Epic task = new Epic("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        SubTask task1 = new SubTask("Test 3", "Testing task 3", 1,
                LocalDateTime.now().plus(Duration.ofHours(6)), Duration.ofMinutes(5));
        SubTask task2 = new SubTask("Test 4", "Testing task 4", 1,
                LocalDateTime.now().plus(Duration.ofHours(7)), Duration.ofMinutes(5));
        manager.addEpic(task);
        manager.addSubTask(task1);
        manager.addSubTask(task2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(204, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        ArrayList<SubTask> tasksFromManager = manager.getAllSubtasks();

        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteOneSubTask() throws IOException, InterruptedException {
        // создаём задачу
        Epic task = new Epic("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        SubTask task1 = new SubTask("Test 3", "Testing task 3", StateTask.NEW, 1, 2,
                LocalDateTime.now().plus(Duration.ofHours(6)), Duration.ofMinutes(5));
        SubTask task2 = new SubTask("Test 4", "Testing task 4", StateTask.NEW, 1, 3,
                LocalDateTime.now().plus(Duration.ofHours(7)), Duration.ofMinutes(5));
        manager.addEpic(task);
        manager.addSubTask(task1);
        manager.addSubTask(task2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(204, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        ArrayList<SubTask> tasksFromManager = manager.getAllSubtasks();
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }
}