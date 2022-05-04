/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.decorator;

import com.google.common.collect.Lists;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.io.paths.MCPath;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.umlmodifier._ast.ASTModifier;
import umlp.UMLPMill;

import static de.monticore.generating.GeneratorEngine.existsHandwrittenClass;
import static de.se_rwth.commons.Names.constructQualifiedName;

/**
 * TODO: This is a local copy of MC's class. Currently the MC class cannot be
 * used because it would cause version conflicts of MC-generator. Furthermore,
 * the class has an adjustment from the original to support the default package
 * of CD4A.
 * TODO: Relocate to CD4A?
 */
public class TopDecorator {

  /*
  Adds the suffix TOP to hand coded ASTs and makes generated TOP class abstract
  Attention! does not actually create a new CD object, because then the glex
  has the wrong objects referenced
   */
  public static final String TOP_SUFFIX = "TOP";

  protected final MCPath hwPath;

  protected static final ASTMCQualifiedName DEFAULT_PACKAGE = MCBasicTypesMill
      .mCQualifiedNameBuilder()
      .setPartsList(Lists.newArrayList("de", "monticore")).build();

  public TopDecorator(MCPath hwPath) {
    this.hwPath = hwPath;
  }

  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit originalCD) {
    ASTCDCompilationUnit topCD = originalCD;
    if (!topCD.isPresentMCPackageDeclaration()) {
      topCD.setMCPackageDeclaration(UMLPMill.mCPackageDeclarationBuilder()
          .setMCQualifiedName(DEFAULT_PACKAGE).build());
    }
    topCD.getCDDefinition().getCDClassesList().stream()
        .filter(cdClass -> existsHandwrittenClass(hwPath,
            constructQualifiedName(topCD.getCDPackageList(), cdClass.getName())))
        .forEach(this::applyTopMechanism);

    topCD.getCDDefinition().getCDInterfacesList().stream()
        .filter(cdInterface -> existsHandwrittenClass(hwPath,
            constructQualifiedName(topCD.getCDPackageList(), cdInterface.getName())))
        .forEach(this::applyTopMechanism);

    topCD.getCDDefinition().getCDEnumsList().stream()
        .filter(cdEnum -> existsHandwrittenClass(hwPath,
            constructQualifiedName(topCD.getCDPackageList(), cdEnum.getName())))
        .forEach(this::applyTopMechanism);

    return topCD;
  }

  protected void applyTopMechanism(ASTCDClass cdClass) {
    makeAbstract(cdClass);
    cdClass.setName(cdClass.getName() + TOP_SUFFIX);

    cdClass.getCDConstructorList().forEach(constructor ->
        constructor.setName(constructor.getName() + TOP_SUFFIX));
  }

  protected void applyTopMechanism(ASTCDInterface cdInterface) {
    cdInterface.setName(cdInterface.getName() + TOP_SUFFIX);
  }

  protected void applyTopMechanism(ASTCDEnum cdEnum) {
    cdEnum.setName(cdEnum.getName() + TOP_SUFFIX);
  }

  protected void makeAbstract(ASTCDType type) {
    makeAbstract(type.getModifier());
  }

  protected void makeAbstract(ASTModifier modifier) {
    modifier.setAbstract(true);
  }
}
