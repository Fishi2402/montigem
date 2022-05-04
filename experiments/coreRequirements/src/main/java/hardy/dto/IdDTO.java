/* (c) https://github.com/MontiCore/monticore */

package hardy.dto;

public class IdDTO extends DTO {

  protected long id = 0;

  protected IdDTO() {
    super("IdDTO");
  }

  public IdDTO(long id) {
    this();
    this.id = id;
  }

  public long getId() {
    return this.id;
  }

  public void setId(long id) {
    this.id = id;
  }

}
