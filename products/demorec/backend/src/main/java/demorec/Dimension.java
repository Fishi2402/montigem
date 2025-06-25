package demorec;

public class Dimension extends DimensionTOP {

  public String getDisplay() {
    return this.getName() + " ("+this.getUnit()+")";
  }
}
