/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.coco;

import de.monticore.guidsl._cocos.GUIDSLCoCoChecker;
import de.monticore.guidsl._cocos.GUIDSLCoCos;

public class GUIDSLCoCosForUMLP extends GUIDSLCoCos {

  public GUIDSLCoCoChecker createChecker() {
    GUIDSLCoCoChecker checker = super.createChecker();
    checker.addCoCo(new GuiNoSingletonList());
    return checker;
  }
}
