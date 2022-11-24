<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName")}

${cd4c.method("public void add${name?cap_first}(int index, ${targetName} o, NotificationScope scope)")}
if (o == null) {
  throw new IllegalArgumentException("Null argument not allowed here");
}
if (!this.${name}.contains(o)) {
  this._internal_add${name?cap_first}(index, java.util.Collections.singletonList(o.getGemId()));
}