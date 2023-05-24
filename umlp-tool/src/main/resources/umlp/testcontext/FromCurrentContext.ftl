<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "relevantClasses")}
${cd4c.method("public static ${cdName}TestContext fromCurrentContext()")}
return new ${cdName}TestContext(
  ${cdName}Manager.getSingleton(),
  CommandManager.getInstance(),
  GeneralErrorCollector.getInstance()
  <#list relevantClasses as c>
  , ${c.name}ErrorCollector.getInstance()
  </#list>
);
