/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension;

import org.jsweet.transpiler.extension.PrinterAdapter;
import umlp.jsweet.extension.common.BasicsAdapter;
import umlp.jsweet.extension.common.UMLPEnumAdapter;
import umlp.jsweet.extension.gui.UMLPGUICollectionsAdapter;
import umlp.jsweet.extension.cd.JavaBasicsAdapter;
import umlp.jsweet.extension.cd.UMLPRTEImportAdapter;
import umlp.jsweet.extension.umlp.JavaStreamAdapter;
import umlp.jsweet.extension.umlp.JavaTimeAdapter;

public class CDAdapter extends PrinterAdapter {
  public CDAdapter(PrinterAdapter parentAdapter) {
    super(
        new JavaStreamAdapter(
            new UMLPGUICollectionsAdapter(
                new UMLPRTEImportAdapter(
                    new JavaTimeAdapter(
                        new JavaBasicsAdapter(
                            new BasicsAdapter(
                                new UMLPEnumAdapter(
                                    parentAdapter
                                )
                            )
                        )
                    )
                )
            )
        )
    );
  }
}
