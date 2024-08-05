/* (c) https://github.com/MontiCore/monticore */
package example_basic;

import def.js.Array;
import domain.Person;
import mc.fenix.basic.gemtreeviewtypes.GemTreeviewEntry;
import umlp.guiutil.UMLPGuiArray;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class GemTreeviewExample extends GemTreeviewExampleTOP {

  @Override
  public List<GemTreeviewEntry> getTreeItems() {
    List<Person> persons = getPersons();
    if (persons == null) {
      persons = new ArrayList<>();
    }
    List<GemTreeviewEntry> result = new ArrayList<>();

    for (Person p : persons) {
      GemTreeviewEntry entry = new GemTreeviewEntry();
      result.add(entry);

      entry.setName(p.getName());
      entry.setChildrenList(new UMLPGuiArray<>());

      for (Person f : p.getFriendsList()) {
        GemTreeviewEntry friend = new GemTreeviewEntry();
        friend.setName(f.getName());
        entry.addChildren(friend);
      }
    }

    return result;
  }

}
