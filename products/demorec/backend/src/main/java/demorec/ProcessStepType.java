package demorec;

public enum ProcessStepType {

  DECONSTRUCTION

  ,
  OTHER


  ;
  private int attr = 0;

  public String toString() {
    switch(this) {
      case DECONSTRUCTION:
        return "Deconstruction";
      default:
        return "Other";
    }
  }
}
