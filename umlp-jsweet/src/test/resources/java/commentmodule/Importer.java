package commentmodule;

public class Importer {

  public void test(Object foo) {
    Object bar = (commentmodule.Imported1) foo;
    test((commentmodule.Imported2) foo);
  }

}