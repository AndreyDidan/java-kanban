package task.manager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.manager.model.Epic;
import task.manager.model.StateTask;
import task.manager.model.SubTask;
import task.manager.model.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest extends TaskManagerTest<TaskManager> {

    private File file;
    private final String startLine = "id,type,name,status,description,epic,startTime,duration";

    @BeforeEach
    void beforeEach() {
        createTaskManager();
    }

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            Path path = Files.createTempFile("tasks", ".csv");
            this.file = path.toFile();
            return new FileBackedTaskManager(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test
    void saveToEmptyFile() {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        Task task = new Task("name", "desc");
        fileBackedTaskManager.addTask(task);

        Epic epic = new Epic("e", "d");
        fileBackedTaskManager.addEpic(epic);
        SubTask subTask = new SubTask("s", "d", epic.getId());
        fileBackedTaskManager.addSubTask(subTask);

        fileBackedTaskManager.getTaskId(task.getId());
        fileBackedTaskManager.getEpicId(epic.getId());
        fileBackedTaskManager.getSubTaskId(subTask.getId());

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            List<String> lines = new ArrayList<>();
            while (bufferedReader.ready()) {
                lines.add(bufferedReader.readLine());
            }

            assertEquals("id,type,name,status,description,epic,startTime,duration", lines.get(0));
            assertEquals("1,TASK,name,NEW,desc,null,null,null", lines.get(1));
            assertEquals("2,EPIC,e,NEW,d,null,null,null", lines.get(2));
            assertEquals("3,SUBTASK,s,NEW,d,2,null,null", lines.get(3));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void loadFromFile_empty() {
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(0, fileBackedTaskManager.getAllTasks().size());
        assertEquals(0, fileBackedTaskManager.getAllEpics().size());
        assertEquals(0, fileBackedTaskManager.getAllSubtasks().size());
        assertEquals(0, fileBackedTaskManager.getHistory().size());
    }

    @Test
    void loadFromFile_filled() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write("id,type,name,status,description,epic,startTime,duration\n" +
                    "1,TASK,name,NEW,desc,null,null,null\n" +
                    "2,EPIC,e,IN_PROGRESS,d,null,null,null\n" +
                    "3,SUBTASK,s,IN_PROGRESS,d,2,null,null\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(1, fileBackedTaskManager.getAllTasks().size());
        assertEquals(1, fileBackedTaskManager.getAllEpics().size());
        assertEquals(1, fileBackedTaskManager.getAllSubtasks().size());
    }

    @Test
    void newFileManagerAndloadFromFile() {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        Task task = new Task("Таска1", "Описание1");
        fileBackedTaskManager.addTask(task);
        Epic epic = new Epic("Эпик2", "Описание2");
        fileBackedTaskManager.addEpic(epic);
        SubTask subTask = new SubTask("Сабтаска3", "Описание3", epic.getId());
        fileBackedTaskManager.addSubTask(subTask);
        Task task1 = new Task("Таска1", "Описание1");
        fileBackedTaskManager.addTask(task1);

        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);

        assertEquals(fileBackedTaskManager.getTaskId(1), fileBackedTaskManager1.getTaskId(1));
        assertEquals(fileBackedTaskManager.getEpicId(2), fileBackedTaskManager1.getEpicId(2));
        assertEquals(fileBackedTaskManager.getSubTaskId(3), fileBackedTaskManager1.getSubTaskId(3));
        assertEquals(fileBackedTaskManager.getAllTasks(), fileBackedTaskManager1.getAllTasks());
    }

    @Test
    void updateTasks() {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        Task task = new Task("name", "desc");
        fileBackedTaskManager.addTask(task);

        Epic epic = new Epic("e", "d");
        fileBackedTaskManager.addEpic(epic);
        SubTask subTask = new SubTask("s", "d", StateTask.IN_PROGRESS, epic.getId(), 2);
        fileBackedTaskManager.addSubTask(subTask);

        Task taskFromManager = fileBackedTaskManager.getTaskId(task.getId());
        Epic epicFromManager = fileBackedTaskManager.getEpicId(epic.getId());
        SubTask subTaskFromManager = fileBackedTaskManager.getSubTaskId(subTask.getId());

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            List<String> lines = new ArrayList<>();
            while (bufferedReader.ready()) {
                lines.add(bufferedReader.readLine());
            }

            assertEquals("id,type,name,status,description,epic,startTime,duration", lines.get(0));
            assertEquals("1,TASK,name,NEW,desc,null,null,null", lines.get(1));
            assertEquals("2,EPIC,e,IN_PROGRESS,d,null,null,null", lines.get(2));
            assertEquals("3,SUBTASK,s,IN_PROGRESS,d,2,null,null", lines.get(3));
        } catch (IOException e) {
            e.printStackTrace();
        }

        taskFromManager.setName("Updated Name");
        fileBackedTaskManager.updateTask(task);
        epicFromManager.setName("Updated Name");
        fileBackedTaskManager.updateEpic(epicFromManager);
        subTaskFromManager.setName("Updated Name");
        fileBackedTaskManager.updateSubTask(subTaskFromManager);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            List<String> lines = new ArrayList<>();
            while (bufferedReader.ready()) {
                lines.add(bufferedReader.readLine());
            }

            assertEquals("id,type,name,status,description,epic,startTime,duration", lines.get(0));
            assertEquals("1,TASK,Updated Name,NEW,desc,null,null,null", lines.get(1));
            assertEquals("2,EPIC,Updated Name,IN_PROGRESS,d,null,null,null", lines.get(2));
            assertEquals("3,SUBTASK,Updated Name,IN_PROGRESS,d,2,null,null", lines.get(3));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteTask() {
        TaskManager taskManager1 = Managers.getFileBackedTaskManager(file);

        Task newTask = new Task("Задача 8", "Описание8",
                LocalDateTime.of(2024, 7, 8, 9, 17), Duration.ofMinutes(10));
        taskManager1.addTask(newTask);
        Epic newEpic = new Epic("Эпик 6", "Описаание эпика 6");
        taskManager1.addEpic(newEpic);
        SubTask newSubTask = new SubTask("Подзадача 7", "Описание подзадачи 7", 2,
                LocalDateTime.of(2024, 7, 8, 11, 17), Duration.ofMinutes(30));
        taskManager1.addSubTask(newSubTask);
        SubTask newSubTask1 = new SubTask("Подзадача 10", "Описание подзадачи 10", 2,
                LocalDateTime.of(2024, 7, 8, 10, 0), Duration.ofMinutes(30));
        taskManager1.addSubTask(newSubTask1);
        List<Task> prioritizedTask1 = taskManager1.getPrioritizedTask();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            List<String> lines = new ArrayList<>();
            while (bufferedReader.ready()) {
                lines.add(bufferedReader.readLine());
            }

            assertEquals("id,type,name,status,description,epic,startTime,duration", lines.get(0));
            assertEquals("1,TASK,Задача 8,NEW,Описание8,null,2024-07-08T09:17,10", lines.get(1));
            assertEquals("2,EPIC,Эпик 6,NEW,Описаание эпика 6,null,2024-07-08T10:00,60",
                    lines.get(2));
            assertEquals("3,SUBTASK,Подзадача 7,NEW,Описание подзадачи 7,2,2024-07-08T11:17,30", lines.get(3));
            assertEquals("4,SUBTASK,Подзадача 10,NEW,Описание подзадачи 10,2,2024-07-08T10:00,30", lines.get(4));
            assertEquals(3, prioritizedTask1.size());
            assertEquals(newTask, prioritizedTask1.get(0));
            assertEquals(newSubTask1, prioritizedTask1.get(1));
            assertEquals(newSubTask, prioritizedTask1.get(2));
        } catch (IOException e) {
            e.printStackTrace();
        }

        TaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        List<Task> prioritizedTask2 = fileBackedTaskManager.getPrioritizedTask();

        assertEquals(3, prioritizedTask2.size());
        assertEquals(newTask, prioritizedTask2.get(0));
        assertEquals(newSubTask1, prioritizedTask2.get(1));
        assertEquals(newSubTask, prioritizedTask2.get(2));
        assertEquals(prioritizedTask1, prioritizedTask2);

        fileBackedTaskManager.deleteTask(newTask.getId());
        fileBackedTaskManager.deleteEpic(newEpic.getId());

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            List<String> lines = new ArrayList<>();
            while (bufferedReader.ready()) {
                lines.add(bufferedReader.readLine());
            }

            assertEquals(1, lines.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
        prioritizedTask2 = fileBackedTaskManager.getPrioritizedTask();
        assertEquals(0, prioritizedTask2.size());

    }
}