package streams;

import domain.Person;
import jsweet.lang.Erased;

import java.util.ArrayList;
import java.util.List;

public class StreamUsageInvalid {

  @Erased
  public static void myMethod() {
    List<Person> pList = new ArrayList<>();
    pList.stream();
    pList.parallelStream();
  }

  public static void anotherMethod() {
    List<Person> pList = new ArrayList<>();
    pList.stream();
  }
}
