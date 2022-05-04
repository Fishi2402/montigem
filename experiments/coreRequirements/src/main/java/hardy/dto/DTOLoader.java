/* (c) https://github.com/MontiCore/monticore */

package hardy.dto;

import hardy.error.NotImplementedException;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Container for DTO
 * <p>
 * used as base class
 *
 * @param <T>
 */
public abstract class DTOLoader<T extends DTO> {

  /**
   * store the dto, to retrieve it later
   */
  protected Optional<T> dto = Optional.empty();

  /**
   * use the version with an id
   */
  protected Optional<Long> useId = Optional.empty();

  public DTOLoader() throws NoSuchElementException {
  }

  public DTOLoader(long id) throws NoSuchElementException {
    this.useId = Optional.of(id);
  }

  public DTOLoader(T dto) {
    this.dto = Optional.ofNullable(dto);
  }

  public Optional<T> getDTOOptional() {
    if (!this.dto.isPresent()) {
      if (this.useId.isPresent()) {
        setDTO(loadDTO(this.useId.get()));
      }
      else {
        setDTO(loadDTO());
      }
    }

    return this.dto;
  }

  public boolean iDTOPresent() {
    return dto.isPresent();
  }

  public T getDTO() {
    return getDTOOptional().orElseThrow(() -> new RuntimeException("dto is absent"));
  }

  public void setDTO(T dto) {
    this.dto = Optional.ofNullable(dto);
  }

  public T loadDTO(long id) {
    throw new NotImplementedException("getById not implemented");
  }

  public T loadDTO() {
    throw new NotImplementedException("getAll not implemented");
  }

}

