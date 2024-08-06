package server.handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import server.HttpTaskServer;
import task.manager.model.Epic;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import task.manager.model.Task;
import task.manager.service.Managers;
import task.manager.service.TaskManager;
import java.util.List;

public class PrioritizedHandlerTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = Managers.getDefault();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public PrioritizedHandlerTest() throws IOException {
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
    void getPrioritized() throws IOException, InterruptedException {
        Epic task = new Epic("Test 1", "Testing task 2");
        SubTask task1 = new SubTask("Test 3", "Testing task 3", 2, LocalDateTime.now().plus(Duration.ofHours(1)), Duration.ofMinutes(5));
        SubTask task2 = new SubTask("Test 4", "Testing task 4", 2, LocalDateTime.now().plus(Duration.ofHours(2)), Duration.ofMinutes(5));
        Task task3 = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        manager.addTask(task3);
        manager.addEpic(task);
        manager.addSubTask(task1);
        manager.addSubTask(task2);
        List<Task> prioritized1 = manager.getPrioritizedTask();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> prioritized = gson.fromJson(response.body(), new TypeToken<List<Task>>() {}.getType());
        assertEquals(200, response.statusCode());
        assertNotNull(prioritized, "Задачи по приоритету не возвращаются");
        assertEquals(3, prioritized.size(), "Некорректное количество задач в списке приоритетности");
    }
}