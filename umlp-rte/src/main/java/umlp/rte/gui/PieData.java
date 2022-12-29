/* (c) https://github.com/MontiCore/monticore */
package umlp.rte.gui;

// PieData is a class generated from a class diagram modeled for Pie component
/* Consider:
 * PieData {
 *   String description; // description of a group of entries conforming to some condition
 *   int value; // number of entries conforming to some condition
 * }
 */
public class PieData {
  protected String description;
  protected int value;

  public PieData(String description, int value) {
    this.description = description;
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public String getDescription() {
    return description;
  }

  public void setValue(int value) {
    this.value = value;
  }

  public void setDescription(String description) {
    this.description = description;
  }

}
