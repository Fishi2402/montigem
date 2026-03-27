package demorec;

public class JointConnectionKind extends JointConnectionKindTOP {


  public String getName() {

    String connectionType = "Undefined";
    switch(this.getJointConnectionType()) {
      case SCREW:
        connectionType = "Screw";
        break;
      case GLUE:
        connectionType = "Glue";
        break;
      case WELDING:
        connectionType = "Welding";
        break;
      case ELECTRICAL:
        connectionType = "Electrical";
        break;
      case CLIP:
        connectionType = "Clip";
        break;
      case RIVET:
        connectionType = "Rivet";
        break;
      default:
        connectionType = "Undefined";
    }
    connectionType = connectionType.charAt(0)+connectionType.substring(1).toLowerCase();

    boolean first = true;
    String components = "";
    for (DemoRecComponent c : this.getConnectsList()) {
      if(!first) {
        components+="-";
      }
      components+=c.getName();
    }

    return connectionType+"("+components+")";
  }

}
