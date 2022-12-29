/* (c) https://github.com/MontiCore/monticore */
package umlp.command;

import java.util.Optional;

public interface DeSer<T> {
  String serialize(T obj);
  Optional<T> deserialize(String str);
}
