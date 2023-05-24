<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName")}

${cd4c.method("public void add${name?cap_first}1(int index, ${targetName} o, NotificationScope scope)")}
if (o == null) {
  console.error("0x50004 Null argument not allowed here");
}
if (!this._${name}.contains(o.getGemId())) {
  this._internal_add${name?cap_first}2(index, [o.getGemId()]);
}
