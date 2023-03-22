<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
${cd4c.method("public void init(String url,
                                String port,
                                String applicationName,
                                String apiName,
                                String user,
                                String password,
                                String scheme)")}
  doInit(scheme, url, port, applicationName, apiName, user, password);