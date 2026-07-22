<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "relevantClasses", "extendedCDNames")}
${cd4c.method("public static ${cdName}TestContext fromCurrentContext()")}
return new ${cdName}TestContext(
  ${cdName}Manager.getSingleton()
  <#list extendedCDNames as extendedCDName>
  , ${extendedCDName}Manager.getSingleton()
  </#list>
  ,
  CommandManager.getInstance(),
  GeneralErrorCollector.getInstance()
  <#list relevantClasses as c>
  , ${c.name}ErrorCollector.getInstance()
  </#list>
);
