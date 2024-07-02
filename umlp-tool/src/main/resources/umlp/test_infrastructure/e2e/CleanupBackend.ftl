<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
${cd4c.method("@AfterEach void cleanupBackend()")}

DatabaseAccessManager.executeStatement("DROP ALL OBJECTS");
