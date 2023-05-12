<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName")}

${cd4c.method("public void addAll${name?cap_first}s(Collection<${targetName}> l, NotificationScope scope)")}

if (l == null) {
  console.error("0x50001 collection ${name} is null");
}
for (let o of l) {
  this.add${name?cap_first}(o, scope);
}