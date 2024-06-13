package task.manager.service;

import exception.ManagerSaveException;
import task.manager.model.*;

import java.io.*;
import java.util.*;
import java.io.IOException;
import java.util.List;
import java.io.File;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public FileBackedTaskManager(File file) {
        super(new InMemoryHistoryManager());
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            manager.loadFromFile();
            return manager;
        } catch (NumberFormatException e) {
            System.out.println("Ошибка при загрузке из файла");
            e.printStackTrace();
        }
        return null;
    }

    protected static String toString(Task  task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStateTask() + "," + task.getDescription() + "," + task.getIdEpic();
    }

    private Task fromString(String value) {
        try {
            final String[] update = value.split(",");
            String name = update[2];
            String description = update[4];
            StateTask stateTask = StateTask.valueOf(update[3]);
            TaskType type = TaskType.valueOf(update[1]);
            Integer id = Integer.parseInt(update[0]);

            Task task = null;

            switch (type) {
                case TASK:
                    task = new Task(id, name, description, stateTask);
                    task.setId(Integer.parseInt(update[0]));
                    break;
                case EPIC: {
                    task = new Epic(name, description);
                    task.setId(Integer.parseInt(update[0]));
                    break;
                }
                case SUBTASK:
                    Integer idEpic = Integer.parseInt(update[5]);
                    task = new SubTask(name, description, stateTask, idEpic, id);
                    task.setId(Integer.parseInt(update[0]));
                    break;
            }
            return task;
        } catch (NumberFormatException e) {
            System.out.println("Ошибка при добавлении задачи");
            e.printStackTrace();
        }
        return null;
    }

    // Сохранение в файл
    private void save() {
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file, UTF_8))) {
            String titul = "id,type,name,status,description,epic\n";
            writer.write(titul);
            for (HashMap.Entry<Integer, Task> entry : tasks.entrySet()) {
                writer.append(toString(entry.getValue()));
                writer.newLine();
            }
            for (HashMap.Entry<Integer, Epic> entry : epics.entrySet()) {
                writer.append(toString(entry.getValue()));
                writer.newLine();
            }
            for (HashMap.Entry<Integer, SubTask> entry : subTasks.entrySet()) {
                writer.append(toString(entry.getValue()));
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл");
        }
    }

    public void loadFromFile() {
        int maxId = 0;
        boolean isNullLine = false;
        try (final BufferedReader reader = new BufferedReader(new FileReader(file, UTF_8))) {
            reader.readLine();
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.isEmpty() || line.isBlank()) {
                    isNullLine = true;
                    break;
                }

                final Task task = fromString(line);
                final int loadId = task.getId();
                if (maxId < loadId) {
                    maxId = loadId;
                }
                if (task.getType() == TaskType.TASK) {
                    tasks.put(loadId, task);
                    if (line.isEmpty()) {
                        break;
                    }
                } else if (task.getType() == TaskType.EPIC) {
                    epics.put(loadId, (Epic) task);
                    if (line.isEmpty()) {
                        break;
                    }
                } else if (task.getType() == TaskType.SUBTASK) {
                    subTasks.put(loadId, (SubTask) task);
                    if (line.isEmpty()) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла");
        }
        super.id = maxId + 1;
    }

    @Override
    public Task addTask(Task newTask) {
        super.addTask(newTask);
        save();
        return newTask;
    }

    @Override
    public Epic addEpic(Epic newEpic) {
        super.addEpic(newEpic);
        save();
        return newEpic;
    }

    @Override
    public SubTask addSubTask(SubTask newSubTask) {
        super.addSubTask(newSubTask);
        save();
        return newSubTask;
    }

    @Override
    public void updateTask(Task updateTask) {
        super.updateTask(updateTask);
        save();
    }

    @Override
    public void updateSubTask(SubTask updateSubTask) {
        super.updateSubTask(updateSubTask);
        save();
    }

    @Override
    public void updateEpic(Epic updateEpic) {
        super.updateEpic(updateEpic);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubTask() {
        super.deleteAllSubTask();
        save();
    }

    @Override
    public Task getTaskId(int id) {
        Task task = super.getTaskId(id);
        return task;
    }

    @Override
    public Epic getEpicId(int id) {
        Epic epic = super.getEpicId(id);
        return epic;
    }

    @Override
    public SubTask getSubTaskId(int id) {
        SubTask subTask = super.getSubTaskId(id);
        return subTask;
    }

    @Override
    public ArrayList<SubTask> getSubTasksInEpic(int idEpic) {
        ArrayList<SubTask> subTasksInEpic = super.getSubTasksInEpic(idEpic);
        return subTasksInEpic;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> getAll = super.getAllTasks();
        return getAll;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> getAll = super.getAllEpics();
        return getAll;
    }

    @Override
    public ArrayList<SubTask> getAllSubtasks() {
        ArrayList<SubTask> getAll = super.getAllSubtasks();
        return getAll;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = super.getHistory();
        return history;
    }
}