package streams;

import domain.Person;
import jsweet.lang.Erased;

import java.util.ArrayList;
import java.util.List;

@Erased
public class StreamUsageClazzErased {

  public static void myMethod() {
    List<Person> pList = new ArrayList<>();
    pList.stream();
    pList.parallelStream();
  }
}
