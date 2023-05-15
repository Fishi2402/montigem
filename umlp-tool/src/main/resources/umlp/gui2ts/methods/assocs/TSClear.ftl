<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName")}

${cd4c.method("public void clear${name?cap_first}s()")}
for (const element : this._${name}) {
  this.remove${name?cap_first}(element);
}