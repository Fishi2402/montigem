/* (c) https://github.com/MontiCore/monticore */
package umlp;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symboltable.IArtifactScope;
import de.monticore.symboltable.IScope;
import de.monticore.tagging.tags.TagsMill;
import de.monticore.tagging.tags._ast.ASTContext;
import de.monticore.tagging.tags._ast.ASTDefaultIdent;
import de.monticore.tagging.tags._ast.ASTModelElementIdentifier;
import de.monticore.tagging.tags._ast.ASTTag;
import de.monticore.tagging.tags._ast.ASTTagUnit;
import de.monticore.tagging.tags._ast.ASTTargetElement;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.CommandLine;
import umlp._symboltable.IUMLPArtifactScope;
import umlp._visitor.UMLPTraverser;
import umlp.generator.util.SingletonLookUp;
import umlptagdefinition.UMLPTagDefinitionMill;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static umlp.UMLPToolOptions.SYMTAB;

// This class is currently a collection of workarounds
// The goal is move as much as possible into the generated components by either fixing them or adding the functionality
public class TagRelated {

  // We use the Symtab cmd option here to store our tagmodels
  public static void doStoreTagStereos(ASTCDCompilationUnit ast, UMLPTool tool, IUMLPArtifactScope symbolTable, CommandLine cmd) {
    // Extract singleton stereotypes and store them ina
    ASTTagUnit tagUnit = TagsMill.tagUnitBuilder().setName(ast.getCDDefinition().getName()).build();
    UMLPTraverser traverser = UMLPMill.traverser();
    traverser.add4CDBasis(new CDBasisVisitor2() {
      @Override
      public void visit(ASTCDClass node) {
        if (node.getModifier().isPresentStereotype() && node.getModifier().getStereotype().contains("singleton")) {
          ASTTag astTag = TagsMill.simpleTagBuilder().setName("singleton").build();
          // Due to CDs weird getFullName (not required to include the scope name), we have to check against the getScopeName (just as the tagger)
          List<String> scopeDiff = getScopeName(node.getEnclosingScope());
          scopeDiff.add(node.getSymbol().getName());
          // TODO: Expose addTag method for interfaces
          tagUnit.addTags(TagsMill.targetElementBuilder()
              .addModelElementIdentifier(TagsMill.defaultIdentBuilder().setMCQualifiedName(TagsMill.mCQualifiedNameBuilder().setPartsList(scopeDiff).build()).build())
              .addTag(astTag).build());
        }
      }
    });
    ast.accept(traverser);
    SingletonLookUp.addTagFile(tagUnit);
    String s = SYMTAB.getValue(cmd);
    if (s != null) {
      // store tag file to passed file
      Path tagPath = Paths.get(
          s + File.separator
              + Names.getPathFromPackage(symbolTable.getPackageName())
              + File.separator
              + symbolTable.getName()
              + ".tags");
      tool.print(TagsMill.prettyPrint(tagUnit, true), tagPath.toString());
    } else {
      System.out.println(TagsMill.prettyPrint(tagUnit, true));
    }
  }

  public static void initLookup(MCPath importedSymbolPath) {
    Set<File> tagFiles = importedSymbolPath.getEntries().stream().map(x -> x.toFile().listFiles(f -> f.getName().endsWith(".tags"))).filter(Objects::nonNull).flatMap(Arrays::stream).collect(Collectors.toSet());

    if (tagFiles.isEmpty()) return;

    UMLPTagDefinitionMill.init();

    for (File f : tagFiles) {
      try {
        Optional<ASTTagUnit> optional = UMLPTagDefinitionMill.parser().parse(f.toString());
        SingletonLookUp.addTagFile(optional.get());

      }catch (IOException ioException) {
        Log.error("Failed to load tag file " +  f.getName() +" :" + ioException.getMessage(), ioException);
      }
    }

  }

  protected static List<String> getScopeName(IScope scope) {
    // Get the name of a scope within its artifact
    List<String> scopeName = new ArrayList<>();
    while (!(scope instanceof IArtifactScope)) {
      if (!scope.isPresentName()) break;
      scopeName.add(0, scope.getName());
      scope = scope.getEnclosingScope();
    }
    return scopeName;
  }


  // Copy of UMLPTagger.getTags, which uses getScopeName()
  public static List<ASTTag> getTags(TypeSymbol symbol, ASTTagUnit astTagUnit) {
    List<ASTTag> tags = new ArrayList<>();
    List<String> scopeStack = getScopeName(symbol.getEnclosingScope());
    if (scopeStack.isEmpty()) {
      findTargetsBy(astTagUnit, symbol.getName()).forEach((t) -> {
        tags.addAll(t.getTagList());
      });
    } else {
      scopeStack.add(symbol.getName());
      List<ASTContext> contexts = findContextBy(astTagUnit, scopeStack.get(0)).collect(Collectors.toList());
      String joinedNames = Joiners.DOT.join(scopeStack);
      findTargetsBy(astTagUnit, joinedNames).forEach((t) -> {
        tags.addAll(t.getTagList());
      });
      scopeStack.remove(0);

      while (scopeStack.size() > 1) {
        List<ASTContext> tempContexts = contexts;
        contexts = new ArrayList<>();
        joinedNames = Joiners.DOT.join(scopeStack);
        String name = scopeStack.remove(0);

        for (ASTContext context : tempContexts) {
          findContextBy(context, name).forEach(contexts::add);
          findTargetsBy(context, joinedNames).forEach((t) -> {
            tags.addAll(t.getTagList());
          });
        }
      }


      for (ASTContext context : contexts) {
        findTargetsBy(context, scopeStack.get(0)).forEach((t) -> {
          tags.addAll(t.getTagList());
        });
      }
    }
    return tags;
  }

  protected static Stream<ASTTargetElement> findTargetsBy(ASTContext astContext, String symbolName) {
    return astContext.getTagsList().stream()
        .filter(t -> t.getModelElementIdentifierList().stream().anyMatch(i -> isIdentified(i, symbolName)));
  }

  protected static Stream<ASTTargetElement> findTargetsBy(ASTTagUnit astTag, String symbolName) {
    return astTag.getTagsList().stream()
        .filter(t -> t.getModelElementIdentifierList().stream().anyMatch(i -> isIdentified(i, symbolName)));
  }

  public static boolean isIdentified(ASTModelElementIdentifier elementIdentifier, String string) {
    if (elementIdentifier instanceof ASTDefaultIdent) {
      return ((ASTDefaultIdent) elementIdentifier).getMCQualifiedName().toString().equals(string);
    }
    return false;
  }

  protected static Stream<ASTContext> findContextBy(ASTTagUnit astTag, String symbolName) {
    return astTag.getContextsList().stream()
        .filter(c -> isIdentified(c.getModelElementIdentifier(), symbolName));
  }

  protected static Stream<ASTContext> findContextBy(ASTContext astTag, String symbolName) {
    return astTag.getContextsList().stream()
        .filter(c -> isIdentified(c.getModelElementIdentifier(), symbolName));
  }
}
