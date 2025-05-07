/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.domain.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.logging.Log;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.rte.data.RTEDeSerDecorator;
import umlp.generator.util.PathUtil;
import umlp.generator.util.TagLookUp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class BaseClassObjectMapperDecorator extends AbstractDecorator {

  public static final String SUFFIX = "BaseClassObjectMapper";

  public BaseClassObjectMapperDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  public final void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    astcdPackage.addCDElement(withOrigin(createClass(origCD)));
  }

  protected ASTCDClass createClass(ASTCDCompilationUnit origCD) {
    String name = origCD.getCDDefinition().getName();

    ASTCDClass mapperClass = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setName(name+SUFFIX)
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("BaseClassObjectMapper"))
            .build();

    List<String> classesList = getNonDerivedDomainClasses(origCD)
        .stream()
        .map(c -> Joiners.DOT.join(getPackageName(origCD, DEFAULT_PACKAGE), c.getName()))
        .collect(Collectors.toList());

    // constructor
    getCD4C().addConstructor(mapperClass, getConstructorTemplate(),
        new ArrayList<>(), name+SUFFIX);

    Map<String, String> deserInits = getNecessaryImportToDeSerMap(origCD);

    // add methods
    getCD4C().addMethod(mapperClass, getInitTemplate(), name, deserInits.values());

    deserInits.keySet().forEach(deserImport -> {
      getCD4C().addImport(mapperClass, deserImport);
    });

    addImports(mapperClass);

    return mapperClass;
  }

  protected String getConstructorTemplate() {
    return "umlp.baseclassobjectmapper.Constructor";
  }

  protected String getInitTemplate() {
    return "umlp.baseclassobjectmapper.Init";
  }

  protected void addImports(ASTCDType clazz) {
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("BaseClassObjectMapper"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("DeSer"));
    getCD4C().addImport(clazz, "java.util.Map");
  }

  protected Map<String, String> getNecessaryImportToDeSerMap(ASTCDCompilationUnit compilationUnit) {
    Map<String, String> deSerNames = new HashMap<>();
    compilationUnit.getMCImportStatementList().forEach(i -> {
      Optional<CDTypeSymbol> symbol = compilationUnit.getEnclosingScope().resolveCDType(i.getQName());

      if(symbol.isPresent() && TagLookUp.isPojo(symbol.get())) {
        Log.info("Found POJO Symbol, adding initialization logic for DeSers.", this.getClass().getName());

        String fullyQualifiedCdName = symbol.get().getPackageName();
        String[] parts = fullyQualifiedCdName.split("\\.");
        String simpleCDName = parts[parts.length-1];
        String simpleDeserName = simpleCDName+RTEDeSerDecorator.SUFFIX;
        String fullyQualifiedDeSerName = Joiners.DOT.join(fullyQualifiedCdName.toLowerCase(),
            simpleDeserName);
        deSerNames.put(fullyQualifiedDeSerName, simpleDeserName);
      }
    });
    return deSerNames;
  }

}
