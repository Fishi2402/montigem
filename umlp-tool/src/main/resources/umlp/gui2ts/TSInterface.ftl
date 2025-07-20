<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("package")}
<#-- ASTCDInterface ast -->
/* (c) https://github.com/MontiCore/monticore */
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>

${defineHookPoint("InterfaceContent:addComment")}

${cdPrinter.printImportList(cd4c.getImportList(ast))}

<#-- Imports hook -->
${defineHookPoint("InterfaceContent:Imports")}

<#-- Imports hook -->
${tc.include("umlp.gui2ts.TSEmptyImports", ast)}
<#-- TODO: issue monticore#4478 -->
${tc.include("umlp.gui2ts.TSEmptyImportsNoAST")}

${tc.include("cd2java.Annotations")}

<#-- Annotations hook -->
${defineHookPoint("InterfaceContent:Annotations")}

${tc.include("umlp.gui2ts.TSInterfaceDeclaration", ast)}