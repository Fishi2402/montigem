package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.se_rwth.commons.Joiners;
import umlp.UMLPMill;
import umlp.generator.domain.decorator.ClientDelegationManagerDecorator;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.util.PathUtil;
import umlp.generator.util.UMLPSymbolTables;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class TSClientDelegationManagerDecorator extends ClientDelegationManagerDecorator {

  public TSClientDelegationManagerDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  protected String getInitTemplate() {
    return "umlp.gui2ts.managerclientimpl.TSInit";
  }

  protected String getDelegatedOptTemplate() {
    return "umlp.gui2ts.delegationmanager.DelegatedOpt";
  }

  protected String getDelegatedGetListTemplate() {
    return "umlp.gui2ts.delegationmanager.DelegatedGetList";
  }

}
