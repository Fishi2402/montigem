/* (c) https://github.com/MontiCore/monticore */
package example_basic;

import domain.Person;
import mc.fenix.basic.gemtreeviewtypes.GemTreeviewEntry;
import umlp.guiutil.HTMLUtils;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
      entry.setChildrenList(new ArrayList<>());

      for (Person f : p.getFriendsList()) {
        GemTreeviewEntry friend = new GemTreeviewEntry();
        friend.setName(f.getName());
        entry.addChildren(friend);
      }
    }

    return result;
  }

  @Override
  public Consumer<GemTreeviewEntry> sayHello() {
    return (Void) -> HTMLUtils.alert("hello");
  }
}
