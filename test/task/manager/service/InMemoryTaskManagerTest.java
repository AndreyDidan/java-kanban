package task.manager.service;

import static org.junit.jupiter.api.Assertions.*;

import exception.ValidationException;
import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {

    private InMemoryTaskManager taskManager;

    @Override
    protected TaskManager createTaskManager() {
        return Managers.getDefault();
    }

    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    }

    @Test
    void setEpicEndTimeAndStartTime_shouldSetAccordingToItsSubTasks() {
        Epic epic = new Epic("Эпик 19", "Описание 19");
        SubTask subTask1 = new SubTask("Подзадача 22", "Описание 22", 1,
                LocalDateTime.of(2024, 5, 2, 11, 15), Duration.ofMinutes(15));
        SubTask subTask2 = new SubTask("Подзадача 23", "Описание 23", 1,
                LocalDateTime.of(2024, 5, 2, 11, 31), Duration.ofMinutes(15));
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        assertEquals(Duration.ofMinutes(30), epic.getDuration());
        assertEquals(subTask1.getStartTime(), epic.getStartTime());
        assertEquals(subTask2.getEndTime(), epic.getEndTime());
    }

    @Test
    void setEpicEndTimeAndStartTime_shouldSetEpicsStartTimeDurationAndEndTimeToNullWhenSubTasksAreDeleted() {
        Epic epic = new Epic("Эпик 20", "Описание 20");
        SubTask subTask1 = new SubTask("Подзадача 23", "Описание 23", 0,
                LocalDateTime.of(2024, 5, 2, 11, 45), Duration.ofMinutes(15));
        SubTask subTask2 = new SubTask("Подзадача 24", "Описание 24", 0,
                LocalDateTime.of(2024, 5, 2, 12, 0), Duration.ofMinutes(15));
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        taskManager.deleteAllSubTask();

        assertNull(epic.getDuration());
        assertNull(epic.getStartTime());
        assertNull(epic.getEndTime());
    }

    @Test
    void checkCollisions_shouldTrowTimeCollisionExceptionWhenTasksTimeCollide_Case1() {
        Task task1 = new Task("Задача 1", "Описание 1",
                LocalDateTime.of(2024, 5, 4, 1, 0), Duration.ofMinutes(15));
        Task task2 = new Task("Задача 2", "Описание 2",
                LocalDateTime.of(2024, 5, 4, 1, 7), Duration.ofMinutes(15));
        taskManager.addTask(task1);

        assertThrows(ValidationException.class, () -> taskManager.addTask(task2));
    }

    @Test
    void checkCollisions_shouldTrowTimeCollisionExceptionWhenTasksTimeCollide_Case2() {
        Task task1 = new Task("Задача 1", "Описание 1",
                LocalDateTime.of(2024, 5, 4, 1, 0), Duration.ofMinutes(15));
        Task task2 = new Task("Задача 2", "Описание 2",
                LocalDateTime.of(2024, 5, 4, 0, 55), Duration.ofMinutes(15));
        taskManager.addTask(task1);

        assertThrows(ValidationException.class, () -> taskManager.addTask(task2));
    }

    @Test
    void checkCollisions_shouldTrowTimeCollisionExceptionWhenTasksTimeCollide_Case3() {
        Task task1 = new Task("Задача 1", "Описание 1",
                LocalDateTime.of(2024, 5, 4, 1, 0), Duration.ofMinutes(15));
        Task task2 = new Task("Задача 2", "Описание 2",
                LocalDateTime.of(2024, 5, 4, 0, 55), Duration.ofMinutes(60));
        taskManager.addTask(task1);

        assertThrows(ValidationException.class, () -> taskManager.addTask(task2));
    }

    @Test
    void checkCollisions_shouldTrowTimeCollisionExceptionWhenTasksTimeCollide_Case4() {
        Task task1 = new Task("Задача 1", "Описание 1",
                LocalDateTime.of(2024, 5, 4, 0, 0), Duration.ofMinutes(15));
        Task task2 = new Task("Задача 2", "Описание 2",
                LocalDateTime.of(2024, 5, 4, 0, 1), Duration.ofMinutes(5));
        taskManager.addTask(task1);

        assertThrows(ValidationException.class, () -> taskManager.addTask(task2));
    }
}