<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "component", "body", "parameters")}
${cd4c.method("protected ${component} ${methodName}()")}
    String typeNames = "";
    <#list parameters?keys as entry>
    if (!${entry}.isBuildFinished()) {
      typeNames += "${parameters[entry]} ";
    }
    </#list>
    if (!typeNames.isEmpty()) {
      // TODO use better strategy both for testing and GUI implementation
      throw new IllegalStateException("0xU1234: " + typeNames + " cannot be built.");
    }
    return new ${component}(${body});