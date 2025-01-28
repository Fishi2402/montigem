/* (c) https://github.com/MontiCore/monticore */
package gui.test;

import umlp.jsweet.extension.annotation.Component;

@Component
public class TestMethodInvocation extends TestMethodInvocationTOP {
  String var = "0";
  public String get0() {
    return get1(get2(var));
  }

  public String get1(String x) {
    return "0";
  }

  public String get2(String x) {
    return "0";
  }
}
