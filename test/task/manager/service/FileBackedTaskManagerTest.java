package task.manager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File file;

    @BeforeEach
    void beforeEach() {
        try {
            Path path = Files.createTempFile("tasks", ".csv");
            file = path.toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

            assertEquals("id,type,name,status,description,epic", lines.get(0));
            assertEquals("0,TASK,name,NEW,desc,null", lines.get(1));
            assertEquals("1,EPIC,e,NEW,d,null", lines.get(2));
            assertEquals("2,SUBTASK,s,NEW,d,1", lines.get(3));
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
            bufferedWriter.write("id,type,name,status,description,epic\n" +
                    "1,TASK,name,NEW,desc\n" +
                    "2,EPIC,e,IN_PROGRESS,d\n" +
                    "3,SUBTASK,s,IN_PROGRESS,d,2\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(1, fileBackedTaskManager.getAllTasks().size());
        assertEquals(1, fileBackedTaskManager.getAllEpics().size());
        assertEquals(1, fileBackedTaskManager.getAllSubtasks().size());
    }

    @Test
    void updateTasks() {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        Task task = new Task("name", "desc");
        fileBackedTaskManager.addTask(task);

        Epic epic = new Epic("e", "d");
        fileBackedTaskManager.addEpic(epic);
        SubTask subTask = new SubTask("s", "d", epic.getId());
        fileBackedTaskManager.addSubTask(subTask);

        Task taskFromManager = fileBackedTaskManager.getTaskId(task.getId());
        Epic epicFromManager = fileBackedTaskManager.getEpicId(epic.getId());
        SubTask subTaskFromManager = fileBackedTaskManager.getSubTaskId(subTask.getId());

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            List<String> lines = new ArrayList<>();
            while (bufferedReader.ready()) {
                lines.add(bufferedReader.readLine());
            }

            assertEquals("id,type,name,status,description,epic", lines.get(0));
            assertEquals("0,TASK,name,NEW,desc,null", lines.get(1));
            assertEquals("1,EPIC,e,NEW,d,null", lines.get(2));
            assertEquals("2,SUBTASK,s,NEW,d,1", lines.get(3));
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

            assertEquals("id,type,name,status,description,epic", lines.get(0));
            assertEquals("0,TASK,Updated Name,NEW,desc,null", lines.get(1));
            assertEquals("1,EPIC,Updated Name,NEW,d,null", lines.get(2));
            assertEquals("2,SUBTASK,Updated Name,NEW,d,1", lines.get(3));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void removeTasks() {
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

            assertEquals("id,type,name,status,description,epic", lines.get(0));
            assertEquals("0,TASK,name,NEW,desc,null", lines.get(1));
            assertEquals("1,EPIC,e,NEW,d,null", lines.get(2));
            assertEquals("2,SUBTASK,s,NEW,d,1", lines.get(3));
        } catch (IOException e) {
            e.printStackTrace();
        }

        fileBackedTaskManager.deleteTask(task.getId());
        fileBackedTaskManager.deleteEpic(epic.getId());

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            List<String> lines = new ArrayList<>();
            while (bufferedReader.ready()) {
                lines.add(bufferedReader.readLine());
            }

            assertEquals(1, lines.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}