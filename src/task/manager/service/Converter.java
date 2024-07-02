package task.manager.service;

import task.manager.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Converter {
    protected static String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStateTask() + ","
                + task.getDescription() + "," + task.getIdEpic() + "," + task.getStartTime() + "," + task.getDuration();
    }

    protected static Task fromString(String value) {
        try {
            final String[] update = value.split(",");
            LocalDateTime startTimeString = null;
            Duration durationString = null;
            String name = update[2];
            String description = update[4];
            StateTask stateTask = StateTask.valueOf(update[3]);
            TaskType type = TaskType.valueOf(update[1]);
            Integer id = Integer.parseInt(update[0]);
            boolean isStartTime = true;
            boolean isDuration = true;

            if (!update[6].equals("null") && !update[7].equals("null")) {
                startTimeString = LocalDateTime.parse(update[6]);
                durationString = Duration.parse((update[7]));
            }

            if (update[6].equals("null")) {
                isStartTime = false;
            }
            if (update[7].equals("null")) {
                isDuration = false;
            }

            Task task = null;

            switch (type) {
                case TASK:
                    if (!isStartTime && !isDuration) {
                        task = new Task(id, name, description, stateTask);
                        task.setId(Integer.parseInt(update[0]));
                        break;
                    } else {
                        task = new Task(id, name, description, stateTask, startTimeString, durationString);
                        task.setId(Integer.parseInt(update[0]));
                        break;
                    }
                case EPIC: {
                    if (!isStartTime && !isDuration) {
                        task = new Epic(name, description);
                        task.setId(Integer.parseInt(update[0]));
                        break;
                    } else {
                        task = new Epic(name, description, startTimeString, durationString);
                        task.setId(Integer.parseInt(update[0]));
                        break;
                    }
                }
                case SUBTASK:
                    if (!isStartTime && !isDuration) {
                        Integer idEpic = Integer.parseInt(update[5]);
                        task = new SubTask(name, description, stateTask, idEpic, id);
                        task.setId(Integer.parseInt(update[0]));
                        break;
                    } else {
                        Integer idEpic = Integer.parseInt(update[5]);
                        task = new SubTask(name, description, stateTask, idEpic, id, startTimeString, durationString);
                        task.setId(Integer.parseInt(update[0]));
                        break;
                    }
            }
            return task;
        } catch (NumberFormatException e) {
            System.out.println("Ошибка при добавлении задачи");
            e.printStackTrace();
        }
        return null;
    }
}
