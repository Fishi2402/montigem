<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "relevantClasses")}
${cd4c.method("public void activate()")}
  ${cdName}Manager.initMe(manager);
  CommandManager.initMe(commandManager);
  GeneralErrorCollector.initMe(generalErrorCollector);
  <#list relevantClasses as c>
  ${c.name}ErrorCollector.initMe(${c.name?uncap_first}ErrorCollector);
  </#list>
