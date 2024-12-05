import java.util.ArrayList;

public class Epic extends Task{
    private ArrayList<Integer> listSubTaskId = new ArrayList<>();

    public Epic(String taskName, String taskDescriptionl, Statuc taskStatus) {
        super(taskName, taskDescriptionl, taskStatus);
    }

    public void addSubTaskId(Subtask subtask) {
        listSubTaskId.add(subtask.getTaskId());
    }

    public void setSubTasksIdList(ArrayList<Integer> newSubTasksIdList) {
        listSubTaskId.addAll(newSubTasksIdList); //Добавляем id новых подзадач к старым
    }

    public Integer getSubTaskId(int subTaskId) {
        if (listSubTaskId.contains(subTaskId)) {
            //  Если нужно вернуть именно объект Integer из списка, а не просто subTaskId:
            return listSubTaskId.stream().filter(id -> id == subTaskId).findFirst().orElse(null);
        }
        return null; // Или выбросить исключение
    }
    public ArrayList<Integer> getListSubTaskId() {
        ArrayList<Integer> copyList = new ArrayList<>(listSubTaskId);
        return copyList;
    }

    public void removeSubTask(Integer id) {
        listSubTaskId.remove(id);
    }

    public void claerListSubTask() {
        listSubTaskId.clear();
    }





}
