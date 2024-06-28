<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("e2eTestClassName")}
${cd4c.method("public void customize(ConfigurableServletWebServerFactory factory)")}

int port = ${e2eTestClassName}.backendServerPort;
System.out.println("Setting port to " + port);
factory.setPort(port);
