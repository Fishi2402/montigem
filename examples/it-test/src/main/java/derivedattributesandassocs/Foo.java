/* (c) https://github.com/MontiCore/monticore */
package derivedattributesandassocs;

import derivedattributesandassocs.gemlists.BarGemList;
import umlp.backendrte.common.Gem;
import umlp.backendrte.common.GemList;

import java.util.List;
import java.util.Optional;

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
  public Optional<String> getD() {
    return Optional.of("drei");
  }

  @Override
  public List<String> getStr1List() {
    return List.of();
  }

  @Override
  public Optional<String> getStr2() {
    return Optional.empty();
  }

  @Override
  public String getStr3() {
    return "";
  }

  @Override
  public GemList<Bar> getBar1DerivedGemList() {
    return new BarGemList();
  }

  @Override
  public Gem<Bar> getBar2DerivedGem() {
    return null;
  }

  @Override
  public Gem<Bar> getBar3DerivedGem() {
    return null;
  }

}
