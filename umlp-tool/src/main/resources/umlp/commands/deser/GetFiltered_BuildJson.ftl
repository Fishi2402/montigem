<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("command")}
${cd4c.method("public void buildJson(${command} command, JsonPrinter printer)")}

super.buildJson(command, printer);
if (command.getFilter().isPresent()) {
  printer.member("filter", command.getFilter());
}