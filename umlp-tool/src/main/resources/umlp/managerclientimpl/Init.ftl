<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cDName", "managerName")}
${cd4c.method("public static void init()")}
  ${managerName}ClientImpl impl = new ${managerName}ClientImpl();
  ${managerName}.setInstance(impl);