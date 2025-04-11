<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTCDClass ast -->
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>

/* (c) https://github.com/MontiCore/monticore */
${defineHookPoint("ClassContent:addComment")}

${cdPrinter.printImportList(cd4c.getImportList(ast))}
<#-- Imports hook -->
${tc.include("umlp.gui2ts.TSEmptyImports", ast)}
<#-- TODO: issue monticore#4478 -->
${tc.include("umlp.gui2ts.TSEmptyImportsNoAST")}

${tc.include("umlp.gui2ts.TSAnnotations")}

<#-- Annotations hook -->
${defineHookPoint("ClassContent:Annotations")}

${tc.include("umlp.gui2ts.TSClassDeclaration", ast)}