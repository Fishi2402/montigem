<#-- (c) https://github.com/MontiCore/monticore -->
${cd4c.method("public void customize(ConfigurableServletWebServerFactory factory)")}

int port = AbstractPlaywrightE2ETest.backendServerPort;
System.out.println("Setting port to " + port);
factory.setPort(port);
