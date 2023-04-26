package umlp.generator.gui.gentypescript.printer;

import de.monticore.types.mcfullgenerictypes._prettyprint.MCFullGenericTypesFullPrettyPrinter;
import de.monticore.types.mcfullgenerictypes._prettyprint.MCFullGenericTypesPrettyPrinter;

public class TypeScriptTypesFullPrettyPrinter extends MCFullGenericTypesFullPrettyPrinter {

  public TypeScriptTypesFullPrettyPrinter(de.monticore.prettyprint.IndentPrinter printer) {
    super(printer, false);
  }

  @Override
  protected void initializeTraverser(boolean printComments) {
    MCFullGenericTypesPrettyPrinter mCFullGenericTypes = new MCFullGenericTypesPrettyPrinter(getPrinter(), printComments);
    getTraverser().setMCFullGenericTypesHandler(mCFullGenericTypes);
    getTraverser().add4MCFullGenericTypes(mCFullGenericTypes);
    de.monticore.types.mcsimplegenerictypes._prettyprint.MCSimpleGenericTypesPrettyPrinter mCSimpleGenericTypes =
        new de.monticore.types.mcsimplegenerictypes._prettyprint.MCSimpleGenericTypesPrettyPrinter(getPrinter(), printComments);
    getTraverser().setMCSimpleGenericTypesHandler(mCSimpleGenericTypes);
    getTraverser().add4MCSimpleGenericTypes(mCSimpleGenericTypes);
    de.monticore.types.mccollectiontypes._prettyprint.MCCollectionTypesPrettyPrinter mCCollectionTypes =
        new TypeScriptCollectionTypesPrettyPrinter(getPrinter());
    getTraverser().setMCCollectionTypesHandler(mCCollectionTypes);
    getTraverser().add4MCCollectionTypes(mCCollectionTypes);
    de.monticore.types.mcbasictypes._prettyprint.MCBasicTypesPrettyPrinter mCBasicTypes =
        new TypeScriptBasicTypesPrettyPrinter(getPrinter());
    getTraverser().setMCBasicTypesHandler(mCBasicTypes);
    getTraverser().add4MCBasicTypes(mCBasicTypes);
    de.monticore.mcbasics._prettyprint.MCBasicsPrettyPrinter mCBasics =
        new de.monticore.mcbasics._prettyprint.MCBasicsPrettyPrinter(getPrinter(), printComments);
    getTraverser().setMCBasicsHandler(mCBasics);
    getTraverser().add4MCBasics(mCBasics);
  }

}