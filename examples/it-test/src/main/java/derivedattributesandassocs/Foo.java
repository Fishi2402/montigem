package derivedattributesandassocs;

import java.util.List;

public class Foo extends FooTOP{

  @Override
  public int getA() {
    return 42;
  }

  @Override
  public String getB() {
    return "test";
  }

  @Override
  public List<String> getCList() {
    return List.of("eins", "zwei");
  }

  //@Override
  /*public Optional<String> getD() {
    return Optional.of("drei");
  }*/
}
