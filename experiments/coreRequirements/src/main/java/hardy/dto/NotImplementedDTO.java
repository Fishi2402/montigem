/* (c) https://github.com/MontiCore/monticore */

package hardy.dto;

public class NotImplementedDTO extends DTO {

  private String msg;

  protected NotImplementedDTO() {
    super("NotImplementedDTO");
  }

  public NotImplementedDTO(String msg) {
    this();
    this.msg = msg;
  }

  @Override
  public boolean isSuccessful() {
    return false;
  }
}
