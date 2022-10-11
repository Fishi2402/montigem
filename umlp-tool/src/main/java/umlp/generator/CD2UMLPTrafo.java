package umlp.generator;

import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import umlp.UMLPMill;
import umlp._ast.ASTUMLPCompilationUnit;
import umlp._ast.ASTUMLPDef;
import umlp._symboltable.UMLPSymbolTableCompleter;

public class CD2UMLPTrafo {
    public static ASTUMLPCompilationUnit run(ASTCDCompilationUnit cdAST) {
        // use visitor to collect CD parts of the UMLP AST (to avoid casts)
        UMLPElementFinder finder = new UMLPElementFinder();
        finder.run(cdAST);

        // create a UMLPDef  with all the UMLPElements of the CD model...
        ASTUMLPDef umlpDef = UMLPMill.uMLPDefBuilder()
                .setName(cdAST.getCDDefinition().getName())
                .setUMLPElementsList(finder.getUMLPElements())
                .build();

        // ... and add it to a new UMLP compilation unit
        ASTUMLPCompilationUnit umlpAST = UMLPMill.uMLPCompilationUnitBuilder()
                .setUMLPDef(umlpDef)
                .build();

        // set the package
        if (cdAST.isPresentMCPackageDeclaration()) {
            umlpAST.setMCPackageDeclaration(cdAST.getMCPackageDeclaration());
        }

        return umlpAST;
    }

    public static void createUMLPSymTab(ASTUMLPCompilationUnit umlpAST) {
        // create symtab
        UMLPMill.scopesGenitorDelegator().createFromAST(umlpAST);
        umlpAST.accept(new UMLPSymbolTableCompleter(umlpAST).getTraverser());
    }

    public static ASTUMLPCompilationUnit createCompilationUnit(ASTUMLPCompilationUnit umlp) {
        // create an empty UMLP definition
        ASTUMLPDef umlpDef = UMLPMill.uMLPDefBuilder()
                .setName(umlp.getUMLPDef().getName())
                .build();

        // ... and add it to a new UMLP compilation unit
        ASTUMLPCompilationUnit newUMLP = UMLPMill.uMLPCompilationUnitBuilder()
                .setUMLPDef(umlpDef)
                .build();

        // set the package
        if (umlp.isPresentMCPackageDeclaration()) {
            newUMLP.setMCPackageDeclaration(umlp.getMCPackageDeclaration());
        }

        return newUMLP;
    }

}
