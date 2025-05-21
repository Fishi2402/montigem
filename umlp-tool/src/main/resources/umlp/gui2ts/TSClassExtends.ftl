<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type="de.monticore.cd4codebasis._ast.ASTCDClass" -->
<#-- @ftlvariable name="cdPrinter" type="umlp.generator.gui.gentypescript.printer.TSUtilsPrinter" -->

<#if ast.isPresentCDExtendUsage()> extends ${cdPrinter.printType(ast.getCDExtendUsage().getSuperclass(0))} </#if> <#rt><#lt>
