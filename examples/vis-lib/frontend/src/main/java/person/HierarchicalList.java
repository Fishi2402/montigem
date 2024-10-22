/* (c) https://github.com/MontiCore/monticore */
package person;

import domain.Person;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HierarchicalList extends HierarchicalListTOP {
  @Override
  protected void initList() {
    this._list = new ArrayList<>();
    super.initList();
  }

  @Override
  public void setList(List<List<Person>> val) {
    super.setList(val);
  }
}
