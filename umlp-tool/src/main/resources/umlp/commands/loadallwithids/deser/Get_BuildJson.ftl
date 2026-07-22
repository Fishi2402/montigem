<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cmdName")}
${cd4c.method("public void buildJson(${cmdName} command, JsonPrinter printer)")}
  super.buildJson(command, printer);
  printer.beginArray("gemIDs");
  for (long gemID : command.getGemIDsList()) {
    printer.value(gemID);
  }
  printer.endArray();
