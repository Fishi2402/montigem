<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cDName", "managerName", "superDelegateManagerNames")}
${cd4c.method("public static void init()")}
  // Init delegate/module managers first
  <#list superDelegateManagerNames as sm>
    ${sm}.init();
  </#list>

  // Then init itself
  ${managerName} impl = new ${managerName}();
  ${cDName}Manager.setInstance(impl);
