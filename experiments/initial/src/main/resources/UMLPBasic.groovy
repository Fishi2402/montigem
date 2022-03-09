import de.monticore.cdbasis._ast.ASTCDCompilationUnit
System.out.println("Hello from sout")
debug("Hello there from base class")

//// Translate AST of UMLP Model into initial CD-AST
//ASTCDCompilationUnit cdAST = UMLP2CDTrafo.run(modelAST);
//logTrafoExec("UMLP2CDTrafo", cdAST);
//
//// execute some basic CD-AST trafos (e.g., put everything in packages)
//cD4CodeAfterParseTrafo.transform(cdAST);
//logTrafoExec("CD4AnalysisAfterParseTrafo", cdAST);
//
//cD4CodeTrafo4Defaults.transform(cdAST);
//logTrafoExec("CD4CodeTrafo4Defaults", cdAST);
//
//cD4CodeDirectCompositionTrafo.transform(cdAST);
//logTrafoExec("CD4CodeDirectCompositionTrafo", cdAST);
//
//cDAssociationCreateFieldsFromNavigableRoles.init(cdAST);
//cDAssociationCreateFieldsFromNavigableRoles.transform(cdAST);
//logTrafoExec("CDAssociationCreateFieldsFromNavigableRoles", cdAST);
//
//// translate the CD-AST into Java with the CD Generator
//cdGenerator.generate(cdAST);
