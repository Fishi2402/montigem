package gui.test;

import jsweettest.C2;
import umlp.jsweet.extension.annotation.Component;

@Component
public class TestEmptyGem extends TestEmptyGemTOP {

  @Override
  public Boolean checkGemForPresence(C2 c2) {
    return c2.getC3optGem().isPresent();
  }

  @Override
  public Boolean checkGemForNonPresence(C2 c2) {
    return !c2.getC3optGem().isPresent();
  }
}
