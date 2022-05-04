/* (c) https://github.com/MontiCore/monticore */

package hardy.command.dto;

import hardy.dto.DTO;
import hardy.dto.ErrorDTO;
import hardy.dto.OkDTO;
import hardy.error.MontiGemError;

/**
 * single result of a executed command
 */
public class CommandResultDTO {
  private long id;
  private DTO dto;

  public CommandResultDTO(long id, MontiGemError error) {
    this.id = id;
    this.dto = new ErrorDTO(error);
  }

  public CommandResultDTO(long id, DTO dto) {
    this.id = id;
    this.dto = dto;
  }

  public CommandResultDTO(long id) {
    this.id = id;
    this.dto = new OkDTO();
  }

  public DTO getDto() {
    return dto;
  }

  public void setDto(DTO dto) {
    this.dto = dto;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }
}
