package demorec.processmodeling.wrapper;

public class MergeTaskSplit extends Split {
  String splitId;
  String mergeId;

  public MergeTaskSplit(String taskId, String mergeId, String splitId) {
    super(taskId);
    this.splitId = splitId;
    this.mergeId = mergeId;
  }

  public String getSplit() {
    return splitId;
  }

  public String getMerge() {
    return mergeId;
  }
}
