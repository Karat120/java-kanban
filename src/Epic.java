import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private ArrayList<Integer> listSubTaskId = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String taskName, String taskDescriptionl) {
        super(taskName, taskDescriptionl, Statuc.NEW);
    }

    public void updateTime(InMemoryTaskManager manager) {
        if (listSubTaskId.isEmpty()) {
            this.startTime = null;
            this.duration = null;
            this.endTime = null;
            return;
        }

        LocalDateTime earliest = null;
        LocalDateTime latest = null;
        Duration total = Duration.ZERO;

        for (Integer subTaskId : listSubTaskId) {
            Subtask subtask = manager.getSubTaskById(subTaskId);
            if (subtask == null || subtask.getStartTime() == null) continue;

            if (earliest == null || subtask.getStartTime().isBefore(earliest)) {
                earliest = subtask.getStartTime();
            }

            LocalDateTime end = subtask.getEndTime();
            if (latest == null || end.isAfter(latest)) {
                latest = end;
            }

            total = total.plus(subtask.getDuration());
        }

        this.startTime = earliest;
        this.duration = total;
        this.endTime = latest;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void addSubTaskId(Subtask subtask) {
        listSubTaskId.add(subtask.getTaskId());
    }

    public ArrayList<Integer> getListSubTaskId() {
        ArrayList<Integer> copyList = new ArrayList<>(listSubTaskId);
        return copyList;
    }

    public void removeSubTaskById(Integer id) {
        listSubTaskId.remove(id);
    }

    public void clearListSubTask() {
        listSubTaskId.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getTaskId() +
                ", name=" + getTaskName() + " , taskDescriptionl='" + super.getTaskDescriptionl() +
                ", subTasksIdList=" + listSubTaskId +
                ", status=" + getTaskStatus() +
                '}';
    }
}
