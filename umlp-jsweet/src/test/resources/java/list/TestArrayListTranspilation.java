package list;

import domain.Person;
import java.util.List;
import java.util.ArrayList;

public class TestArrayListTranspilation {

  public void test() {
    List<Person> personList = new ArrayList();

    personList.stream().forEach(p -> {
      System.out.println(p);
    });
  }
}
