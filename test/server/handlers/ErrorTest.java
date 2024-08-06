package server.handlers;

import com.google.gson.Gson;
import server.HttpTaskServer;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import task.manager.model.Task;
import task.manager.service.Managers;
import task.manager.service.TaskManager;

class ErrorTest {

    TaskManager manager = Managers.getDefault();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public ErrorTest() throws IOException {
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
    void statusCode404() throws IOException, InterruptedException {

        // создаём задачи
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        Task task1 = new Task("Test 3", "Testing task 3", LocalDateTime.now().plus(Duration.ofHours(6)),
                Duration.ofMinutes(5));
        manager.addTask(task);
        manager.addTask(task1);

        // создаём HTTP-клиент и запрос с несущесвующим id
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/10");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
    }

    @Test
    void statusCode405() throws IOException, InterruptedException {

        // создаём задачи
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        Task task1 = new Task("Test 3", "Testing task 3", LocalDateTime.now().plus(Duration.ofHours(6)),
                Duration.ofMinutes(5));
        manager.addTask(task);
        manager.addTask(task1);

        // создаём HTTP-клиент и нереализованный метод запроса
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(405, response.statusCode());
    }

    @Test
    void statusCode406() throws IOException, InterruptedException {

        //Создаём 2 задачи с пересечением по времени, 1 добавляем в менеджер, 2-ю добавляем через сервер
        Task task = new Task("Task 1", "Description 1", LocalDateTime.now(), Duration.ofMinutes(5));
        Task task2 = new Task("Task 2", "Description 2", LocalDateTime.now().plus(Duration.ofMinutes(2)),
                Duration.ofMinutes(5));
        manager.addTask(task);

        String task2Json = gson.toJson(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
    }
}