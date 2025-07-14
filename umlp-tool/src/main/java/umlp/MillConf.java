package umlp;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.tagging.tags.TagsMill;

import java.util.Collections;
import java.util.EnumSet;

public enum MillConf {
  GUIDSL,
  UMLP,
  TAGS;

  public static void switchTo(MillConf... mills) {
    EnumSet<MillConf> es = EnumSet.noneOf(MillConf.class);
    Collections.addAll(es, mills);

    GUIDSLMill.reset();
    TagsMill.reset();
    UMLPMill.reset();

    for (MillConf c : mills) {
      switch (c) {
        case GUIDSL:
          GUIDSLMill.init();
          BasicSymbolsMill.initializePrimitives();
          break;
        case TAGS:
          TagsMill.init();
          break;
        case UMLP:
          UMLPMill.init();
          BuiltInTypes.addBuiltInTypes(UMLPMill.globalScope(), false);
          break;
      }
    }
  }
}
