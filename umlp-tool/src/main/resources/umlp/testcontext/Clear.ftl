<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "relevantClasses")}
${cd4c.method("public static void clear()")}
  ${cdName}Manager.initMe(null);
  CommandManager.initMe(null);
  GeneralErrorCollector.initMe(null);
  <#list relevantClasses as c>
  ${c.name}ErrorCollector.initMe(null);
  </#list>
