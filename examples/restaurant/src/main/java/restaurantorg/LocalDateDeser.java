/* (c) https://github.com/MontiCore/monticore */
package restaurantorg;

import umlp.backendrte.command.DeSer;

import java.time.LocalDate;
import java.util.Optional;

public class LocalDateDeser implements DeSer<LocalDate> {
  @Override
  public String serialize(LocalDate obj) {
    return null;
  }

  @Override
  public Optional<LocalDate> deserialize(String str) {
    return Optional.empty();
  }
}
