<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cDName", "managerName")}
${cd4c.method("public static void init()")}
  ${managerName} impl = new ${managerName}();
  ${cDName}Manager.setInstance(impl);
