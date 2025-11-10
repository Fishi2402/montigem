/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.cd;

import org.jsweet.transpiler.extension.PrinterAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class UMLPRTEImportAdapter extends umlp.jsweet.extension.umlp.UMLPRTEImportAdapter {
  public UMLPRTEImportAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  @Override
  protected List<Pattern> getExcludes() {
    List<Pattern> res = new ArrayList<>(super.getExcludes());
    res.add(Pattern.compile("umlp\\.backendrte\\.common\\.Gem"));
    return res;
  }
}
