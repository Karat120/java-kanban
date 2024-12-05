import java.util.ArrayList;
public class Epic extends Task{
    private ArrayList<Integer> listSubTaskId = new ArrayList<>();

    public Epic(String taskName, String taskDescriptionl, Statuc taskStatus) {
        super(taskName, taskDescriptionl, taskStatus);
    }

    public void addSubTaskId(Subtask subtask) {
        listSubTaskId.add(subtask.getTaskId());
    }

    public Integer getSubTaskId(int subTaskId) {

    }



}
