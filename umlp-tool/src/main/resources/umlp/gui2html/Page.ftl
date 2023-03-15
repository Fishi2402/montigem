<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a html file from the given model -->
${tc.signature("notNestedComponents")}
<#list notNestedComponents as component>
  ${tc.include("umlp.gui2html.Component", component)}
</#list>