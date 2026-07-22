<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "relevantClasses", "extendedCDNames")}
${cd4c.method("public void activate()")}
  <#list extendedCDNames as extendedCDName>
  ${extendedCDName}Manager.initMe(${extendedCDName?uncap_first}Manager);
  </#list>
  ${cdName}Manager.initMe(manager);
  CommandManager.initMe(commandManager);
  GeneralErrorCollector.initMe(generalErrorCollector);
  <#list relevantClasses as c>
  ${c.name}ErrorCollector.initMe(${c.name?uncap_first}ErrorCollector);
  </#list>
