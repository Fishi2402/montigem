<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
${cd4c.method("@AfterEach protected void cleanupBackend()")}

DatabaseAccessManager.executeStatement("DROP ALL OBJECTS");
