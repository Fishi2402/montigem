<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className")}
${cd4c.method("public static void startDB() throws Exception")}
dbStarter.start(${className}::getDBPath);
