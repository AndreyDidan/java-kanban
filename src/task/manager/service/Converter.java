package task.manager.service;

import task.manager.model.*;

public class Converter {
    protected static String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStateTask() + "," + task.getDescription() + "," + task.getIdEpic();
    }

    protected static Task fromString(String value) {
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
}
