package streams;

import domain.Person;

import java.util.ArrayList;
import java.util.List;

public class StreamUsage {

  public static void myMethod() {
    List<Person> pList = new ArrayList<>();
    pList.stream();
    pList.parallelStream();
  }
}
