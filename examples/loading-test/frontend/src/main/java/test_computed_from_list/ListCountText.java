package test_computed_from_list;

import loadingtests.Person;
import umlp.jsweet.extension.annotation.Component;

import java.util.List;

@Component
public class ListCountText extends ListCountTextTOP {
  @Override
  public String formatCount1(List<Person> list) {
    if(list == null) {
      return "0";
    }
    return Integer.toString(list.size());
  }
  @Override
  public String formatCount2() {
    if(getList() == null) {
      return "0";
    }
    return Integer.toString(getList().size());
  }
}
