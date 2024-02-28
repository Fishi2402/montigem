/* (c) https://github.com/MontiCore/monticore */
package umlp;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisVisitor2;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._tagging.CDBasisTagger;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.io.paths.MCPath;
import de.monticore.symboltable.ISymbol;
import de.monticore.tagging.TagRepository;
import de.monticore.tagging.tags.TagsMill;
import de.monticore.tagging.tags._ast.ASTTag;
import de.monticore.tagging.tags._ast.ASTTagUnit;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.CommandLine;
import umlp._symboltable.IUMLPArtifactScope;
import umlp._tagging.UMLPTagger;
import umlp._visitor.UMLPTraverser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Currently tags are used to exchange information given in class diagrams
 * (such as <code><<singleton>></code> StereoTypes on classes)
 *
 * This stereotype information is then copied into a tags file,
 * which is then loaded again during the Gui-generation tasks.
 */
public class TagRelated {

  // We use the Symtab cmd option here to store our tagmodels
  public static void doStoreTagStereos(ASTCDCompilationUnit ast, UMLPTool tool, IUMLPArtifactScope symbolTable, CommandLine cmd) {
    // Extract singleton stereotypes and store them ina
    ASTTagUnit tagUnit = TagRepository.loadTempTagModel(ast.getCDDefinition().getName()).get();
    boolean[] hasTags = new boolean[1];
    UMLPTraverser traverser = UMLPMill.traverser();
    traverser.add4CDBasis(new CDBasisVisitor2() {
      @Override
      public void visit(ASTCDClass node) {
        if (node.getModifier().isPresentStereotype() && node.getModifier().getStereotype().contains("singleton")) {
          ASTTag astTag = TagsMill.simpleTagBuilder().setName("singleton").build();
          hasTags[0] = true;
          __addTag(node.getSymbol(), tagUnit, astTag);
          node.getCDMethodList().forEach(m -> {
            ASTTag astTagCommand = TagsMill.simpleTagBuilder().setName("command").build();
            // Problem: CDMethodSignature => Sehub.clazz.methodname
            // OOSymbols/Method => clazz.methodname
            __addTag(m.getSymbol(), tagUnit, astTagCommand);
            hasTags[0] = true;
          });
        }
        if (node.getModifier().isDerived()) {
          ASTTag astTag = TagsMill.simpleTagBuilder().setName("derived").build();
          hasTags[0] = true;
          __addTag(node.getSymbol(), tagUnit, astTag);
        }
      }
    });
    /*
    traverser.add4CD4CodeBasis(new CD4CodeBasisVisitor2() {
      @Override
      public void visit(ASTCDMethod node) {
        if (node.getModifier().isPresentStereotype() && node.getModifier().getStereotype().contains("command")) {
          ASTTag astTag = TagsMill.simpleTagBuilder().setName("command").build();
          // Problem: CDMethodSignature => Sehub.clazz.methodname
          // OOSymbols/Method => clazz.methodname
          __addTag(node.getSymbol(), tagUnit, astTag);
        }
      }
    });*/
    ast.accept(traverser);

    String s = UMLPToolOptions.SYMTAB.getValue(cmd);
    if (s != null) {
      // store tag file to passed file
      Path tagPath = Paths.get(
          s + File.separator
              + Names.getPathFromPackage(symbolTable.getPackageName())
              + File.separator
              + symbolTable.getName()
              + ".tags");
      tool.print(TagsMill.prettyPrint(tagUnit, true), tagPath.toString());
      Reporting.reportFileCreation(tagPath.toString());

      TagRepository.unloadTagModel(tagUnit);
      try {
        TagRepository.loadTagModel(tagPath.toFile());
      } catch (IOException ignored) {}
    } else {
      System.out.println(TagsMill.prettyPrint(tagUnit, true));
      if (hasTags[0])
        Log.error("symboltable option is required to store tag information");
    }
  }


  public static void initLookup(MCPath importedSymbolPath) {
    Set<File> tagFiles = importedSymbolPath.getEntries().stream()
        .map(x -> x.toFile().listFiles(f -> f.getName().endsWith(".tags")))
        .filter(Objects::nonNull)
        .flatMap(Arrays::stream)
        .collect(Collectors.toSet());

    if (tagFiles.isEmpty()) return;

    // note: The generic Tags language does not support concrete UMLP syntax tagging
    TagsMill.init();

    for (File f : tagFiles) {
      try {
        TagRepository.loadTagModel(f);
      } catch (IOException ioException) {
        Log.error("Failed to load tag file " + f.getName() + " :" + ioException.getMessage(), ioException);
      }
    }

  }

  // TODO: Migrate this upstream into MC:
  //  UMLP: sehub.App.
  protected static void __addTag(ISymbol symbol, ASTTagUnit astTagUnit, ASTTag astTag){
    astTagUnit.addTags(
        TagsMill.targetElementBuilder()
            .addModelElementIdentifier(
                TagsMill.defaultIdentBuilder().setMCQualifiedName(
                    TagsMill.mCQualifiedNameBuilder().setPartsList(de.se_rwth.commons.Splitters.QUALIFIED_NAME_DELIMITERS.splitToList(symbol.getFullName())).build()
                ).build()
            )
            .addTag(astTag)
            .build()
    );

  }
}
