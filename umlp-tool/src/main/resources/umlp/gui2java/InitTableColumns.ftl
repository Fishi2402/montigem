<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("componentName", "expressions", "guiDSLPrinter")}
    List<TableColumn> initialTableColumns${componentName} = new ArrayList();
  <#list expressions as expression>
       initialTableColumns${componentName}.add(new TableColumn(${guiDSLPrinter.prettyprint(expression)});
  </#list>
    setTableColumns${componentName}(initialTableColumns${componentName});
