package demorec;

// TOPing this Enum because of JSweet
public enum JointConnectionType {
  SCREW

  ,
  GLUE

  ,
  WELDING

  ,
  PLUG

  ,
  ELECTRICAL

  ,
  CLIP

  ,
  RIVET

  ,
  UNDEFINED


  ;

  private int attr = 0;

  public String toString() {
    switch(this) {
      case SCREW:
        return "Screw";
      case GLUE:
        return "Glue";
      case WELDING:
        return "Welding";
      case ELECTRICAL:
        return "Electrical";
      case CLIP:
        return "Clip";
      case RIVET:
        return "Rivet";
      default:
      return "Undefined";
    }
  }
}
