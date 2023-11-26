/* (c) https://github.com/MontiCore/monticore */
package umlp;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.io.paths.MCPath;
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

public class TagRelated {

  // We use the Symtab cmd option here to store our tagmodels
  public static void doStoreTagStereos(ASTCDCompilationUnit ast, UMLPTool tool, IUMLPArtifactScope symbolTable, CommandLine cmd) {
    // Extract singleton stereotypes and store them ina
    ASTTagUnit tagUnit = TagRepository.loadTempTagModel(ast.getCDDefinition().getName()).get();
    UMLPTraverser traverser = UMLPMill.traverser();
    traverser.add4CDBasis(new CDBasisVisitor2() {
      @Override
      public void visit(ASTCDClass node) {
        if (node.getModifier().isPresentStereotype() && node.getModifier().getStereotype().contains("singleton")) {
          ASTTag astTag = TagsMill.simpleTagBuilder().setName("singleton").build();
          UMLPTagger.getInstance().addTag((ASTCDType) node, tagUnit, astTag);
        }
      }
    });
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

}
