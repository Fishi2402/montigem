<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTCDClass ast -->

<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>
/* (c) https://github.com/MontiCore/monticore */
${defineHookPoint("ClassContent:addComment")}

${cdPrinter.printImportList(cd4c.getImportList(ast))}

<#-- Imports hook -->
${defineHookPoint("ClassContent:Imports")}

${tc.include("umlp.gui2ts.TSAnnotations")}

<#-- Annotations hook -->
${defineHookPoint("ClassContent:Annotations")}

${tc.include("umlp.gui2ts.TSClassDeclaration", ast)}