<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attr", "listItems")}
${cd4c.method("protected void init${attr.getName()?cap_first}()")}
this.${attr.getName()} = List.of(
  <#list listItems as item>${item}<#sep>,</#list>
);