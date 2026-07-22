<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "relevantClasses", "extendedCDNames")}
${cd4c.method("public static void clear()")}
  <#list extendedCDNames as extendedCDName>
  ${extendedCDName}Manager.initMe(null);
  </#list>
  ${cdName}Manager.initMe(null);
  CommandManager.initMe(null);
  GeneralErrorCollector.initMe(null);
  <#list relevantClasses as c>
  ${c.name}ErrorCollector.initMe(null);
  </#list>
