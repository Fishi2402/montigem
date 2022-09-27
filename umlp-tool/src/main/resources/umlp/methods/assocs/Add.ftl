<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName", "sourceName")}

${cd4c.method("public void add${name?cap_first}(int index, ${targetName} o, NotificationScope scope)")}
if (o == null) {
  throw new IllegalArgumentException("Null argument not allowed here");
}
if (!this.${name}.contains(o)) {
  this.${name}._internal_add(index, Collections.singletonList(o.getGemId()));
  check${name?cap_first}();
  ${sourceName?uncap_first}Observers.forEach(obs -> obs.maybeNotifyAdd${name?cap_first}((${sourceName}) this, o.getGemId(), index, scope));
}