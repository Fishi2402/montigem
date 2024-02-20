<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName")}
${cd4c.method("public Optional<String> getClientName(int index)")}

return clientNames != null && index >= 0 && index < clientNames.size() ?
  Optional.ofNullable(clientNames.get(index))
  : Optional.empty();
