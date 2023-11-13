/* (c) https://github.com/MontiCore/monticore */
package derivedattributesandassocs;

import derivedattributesandassocs.gemlists.BarGemList;
import umlp.backendrte.common.GemList;

import java.util.List;

public class Foo extends FooTOP {

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

  @Override
  public GemList<Bar> getBarDerivedGemList() {
    return new BarGemList();
  }

  //@Override
  /*public Optional<String> getD() {
    return Optional.of("drei");
  }*/
}
