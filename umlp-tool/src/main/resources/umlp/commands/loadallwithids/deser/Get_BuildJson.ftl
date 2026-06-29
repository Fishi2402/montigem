<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cmdName")}
${cd4c.method("public void buildJson(${cmdName} command, JsonPrinter printer)")}
  super.buildJson(command, printer);
  List<String> collect = new ArrayList<>();
  for (Long l : command.getGemIDsList()) {
    String s = String.valueOf(l);
    collect.add(s);
  }
  printer.member("gemIDs", collect);
