/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.Joiners;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class TuplizerDecorator extends AbstractDecorator {

  public static final String SUFFIX = "Tuplizer";

  public TuplizerDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  public final void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    astcdPackage.addCDElement(createClass(origCD));
  }

  protected ASTCDClass createClass(ASTCDCompilationUnit origCD) {
    String name = origCD.getCDDefinition().getName();

    ASTCDClass tuplizerClass = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setName(name+SUFFIX)
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("PojoEntityTuplizer"))
            .build();

    // constructor
    getCD4C().addConstructor(tuplizerClass, "umlp.tuplizer.Constructor", origCD.getCDDefinition().getName());

    String packageName = getPackageName(origCD, DEFAULT_PACKAGE);
    List<String> classNames = origCD.getCDDefinition().getCDClassesList().stream()
            .filter(c -> !c.getModifier().isDerived())
            .map(c -> Joiners.DOT.join(packageName, c.getName())).collect(Collectors.toList());
    getCD4C().addMethod(tuplizerClass, "umlp.tuplizer.DetermineName", classNames);

    addImports(tuplizerClass);

    return tuplizerClass;
  }

  protected void addImports(ASTCDClass ast) {
    getCD4C().addImport(ast, "org.hibernate.engine.spi.SessionFactoryImplementor");
    getCD4C().addImport(ast, "org.hibernate.mapping.PersistentClass");
    getCD4C().addImport(ast, "org.hibernate.tuple.entity.EntityMetamodel");
    getCD4C().addImport(ast, "org.hibernate.tuple.entity.PojoEntityTuplizer");
  }

}
