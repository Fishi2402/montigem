<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("componentName", "expressions")}
    List<TableColumn> initial${componentName?cap_first}Columns = new ArrayList();
  <#list expressions as expression>
       initial${componentName?cap_first}Columns.add(new TableColumn(${expression}));
  </#list>
    set${componentName?cap_first}Columns(initial${componentName?cap_first}Columns);