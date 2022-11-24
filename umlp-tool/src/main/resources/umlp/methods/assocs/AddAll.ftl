<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName")}

${cd4c.method("public void addAll${name?cap_first}s(Collection<${targetName}> l, NotificationScope scope)")}

if (l == null) {
  throw new IllegalArgumentException("Null argument not allowed here");
}
for (${targetName} o : l) {
  add${name?cap_first}(o, scope);
}