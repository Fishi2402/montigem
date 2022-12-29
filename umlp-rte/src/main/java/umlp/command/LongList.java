/* (c) https://github.com/MontiCore/monticore */
package umlp.command;

// RTE-P

import java.util.ArrayList;
import java.util.Collection;

// TODO: ehemals für Gson gebraucht, durch ListCommandResult ersetzten
public class LongList extends ArrayList<Long> {

  public LongList() {
  }

  public LongList(Collection<? extends Long> c) {
    super(c);
  }
}
