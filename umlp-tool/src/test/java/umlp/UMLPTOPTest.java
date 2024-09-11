/* (c) https://github.com/MontiCore/monticore */
package umlp;

import de.monticore.cd.codegen.TopDecorator;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.GeneratorEngine;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.MCFatalError;
import org.gradle.internal.impldep.org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import umlp.generator.gui.GUIGeneratorUtil;
import umlp.generator.gui.gentypescript.java2ts.TSTopDecorator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class UMLPTOPTest {

  Set<String> guiGenFilesWithHWC = new HashSet<>();
  Set<String> javaFilesWithHWC = new HashSet<>();
  List<String> checkedGenEngineFiles = new ArrayList<>();
  List<String> checkedGuiGenUtilFiles = new ArrayList<>();

  protected boolean existsMockedFile(String fqn, Collection<String> fileList, Consumer<String> adder) {
    adder.accept(fqn);
    System.err.println("=> check for " + fqn);
    for (String candidate : fileList)
      if (fqn.equals(candidate))
        return true;
    System.err.println("not found");
    return false;
  }

  @Test
  public void testTOP() {
    Log.initDEBUG();
    try (MockedStatic<GeneratorEngine> engineMock = Mockito.mockStatic(GeneratorEngine.class)) {
      engineMock
          .when(
              () ->
                  GeneratorEngine.existsHandwrittenClass(
                      Mockito.any(MCPath.class), Mockito.any(String.class)))
          .thenAnswer(i -> existsMockedFile(i.getArgument(1, String.class), javaFilesWithHWC, checkedGenEngineFiles::add));
      GUIGeneratorUtil guiGeneratorUtilInstance = Mockito.mock(GUIGeneratorUtil.class);
      try (MockedStatic<GUIGeneratorUtil> genUtilMock = Mockito.mockStatic(GUIGeneratorUtil.class)) {
        genUtilMock.when(GUIGeneratorUtil::getInstance)
            .thenReturn(guiGeneratorUtilInstance);

        // any Exists
        Mockito.when(guiGeneratorUtilInstance.existsHandwrittenClass(Mockito.any(MCPath.class), Mockito.any(String.class)))
            .thenAnswer(i -> existsMockedFile(i.getArgument(1, String.class), guiGenFilesWithHWC, checkedGuiGenUtilFiles::add));


        TSTopDecorator tsTopDecorator = new TSTopDecorator(new MCPath());

        // A simple class -> check that it exists
        var dec = tsTopDecorator.decorate(make(newClass("Class1")));
        Assert.assertEquals(1, getCheckCount("Class1", checkedGenEngineFiles));
        Assert.assertEquals(1, getCheckCount("Class1", checkedGuiGenUtilFiles));

        Log.setErrorHook(() -> {throw new MCFatalError("msg");});
        Assert.assertThrows("0xC0FFEE00: Missing Class2", MCFatalError.class, () -> {
          tsTopDecorator.decorate(make(needsTop(newClass("Class2"), "Missing %s")));
        });
        Log.clearFindings();

        // Class with a hand-written implementation
        dec = tsTopDecorator.decorate(make(needsTop(addHWCGuiGen(newClass("Class3")), "Missing %s")));
        Assert.assertEquals(1, getCheckCount("Class3", checkedGuiGenUtilFiles));


        dec = tsTopDecorator.decorate(make(needsTop(addHWCGuiGen(newClass("Class4Component")), "Missing %s")));
        System.err.println(CD4CodeMill.prettyPrint(dec, false));

      }
    }


  }


  protected ASTCDCompilationUnit make(ASTCDElement... classes) {
    return CD4CodeMill.cDCompilationUnitBuilder()
        .setCDDefinition(CD4CodeMill.cDDefinitionBuilder()
            .setModifier(CD4CodeMill.modifierBuilder().build())
            .setName("TOPTest")
            .addAllCDElements(Arrays.asList(classes))
            .build())
        .build();
  }


  protected ASTCDPackage newPackage(String fqn, ASTCDElement... classes) {
    return CD4CodeMill.cDPackageBuilder()
        .setMCQualifiedName(CD4CodeMill.mCQualifiedNameBuilder().setPartsList(Splitters.DOT.splitToList(fqn)).build())
        .addAllCDElements(Arrays.asList(classes))
        .build();
  }


  protected ASTCDClass newClass(String name) {
    return CD4CodeMill.cDClassBuilder()
        .setModifier(CD4CodeMill.modifierBuilder().build())
        .setName(name)
        .build();
  }

  protected ASTCDClass addHWCGuiGen(ASTCDClass c) {
    guiGenFilesWithHWC.add(c.getName());
    return c;
  }

//  protected ASTCDClass addHWCJava(ASTCDClass c) {
//    javaFilesWithHWC.add(c.getName());
//    return c;
//  }

  protected ASTCDClass needsTop(ASTCDClass astcdClass, String reason) {
    astcdClass.getModifier().setStereotype(
        CD4CodeMill.stereotypeBuilder()
            .addValues(
                CD4CodeMill.stereoValueBuilder()
                    .setName(TopDecorator.NEEDS_TOP_IDENTIFIER)
                    .setContent(reason)
                    .build()
            )
            .build()
    );
    return astcdClass;
  }

  protected long getCheckCount(String str, List<String> list) {
    return list.stream().filter(l -> l.equals(str)).count();
  }

}
