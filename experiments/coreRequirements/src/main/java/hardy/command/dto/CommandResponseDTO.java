/* (c) https://github.com/MontiCore/monticore */

package hardy.command.dto;

import hardy.dto.DTO;
import hardy.dto.OkDTO;
import hardy.error.MontiGemError;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for the results of the processed commands
 */
public class CommandResponseDTO {
  // the unique id of the response, to match with the sent commands
  private final long id;
  private final List<CommandResultDTO> responses;

  public CommandResponseDTO(long id) {
    this.id = id;
    this.responses = new ArrayList<>();
  }

  public CommandResponseDTO(long id, DTO response) {
    this(id);
    this.responses.add(new CommandResultDTO(id, response));
  }

  public CommandResponseDTO(long id, MontiGemError error) {
    this(id);
    this.responses.add(new CommandResultDTO(id, error));
  }

  public long getId() {
    return id;
  }

  public void addResponse(CommandResultDTO response) {
    this.responses.add(response);
  }

  public void addResponse(long id, DTO response) {
    this.responses.add(new CommandResultDTO(id, response));
  }

  public void addResponse(long id, MontiGemError error) {
    this.responses.add(new CommandResultDTO(id, error));
  }

  public void addResponse(long id) {
    this.responses.add(new CommandResultDTO(id, new OkDTO()));
  }

  public List<CommandResultDTO> getResults() {
    return this.responses;
  }

  public void clear() {
    this.responses.clear();
  }

}
