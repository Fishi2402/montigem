/* (c) https://github.com/MontiCore/monticore */
package umlp._prettyprint;

import de.monticore.prettyprint.IndentPrinter;

public  class UMLPFullPrettyPrinter extends UMLPFullPrettyPrinterTOP {

  public UMLPFullPrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  public UMLPFullPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override
  protected void initializeTraverser(boolean printComments) {
    super.initializeTraverser(printComments);
    getTraverser().setCDBasisHandler(new CD2UMLPPrettyPrinter(getPrinter(), printComments));
  }

  public UMLPFullPrettyPrinter() {
    super(new IndentPrinter(), false);
  }
}


