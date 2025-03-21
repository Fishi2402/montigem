/* (c) https://github.com/MontiCore/monticore */
package jsweettest;

import jsweet.lang.Async;
import umlp.jsweet.extension.annotation.Synchronized;

public class JsweettestManagerClientImpl extends JsweettestManagerClientImplTOP {

  @Override
  @Async
  @Synchronized
  protected GlobalState _doGetGlobalState() {
    return super._doGetGlobalState();
  }
}
