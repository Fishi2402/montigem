<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName")}

${cd4c.method("public void clear${name?cap_first}s()")}
Iterator var1 = new ArrayList<>(this.${name}).iterator();
while (var1.hasNext()) {
  ${targetName} element = (${targetName}) var1.next();
  this.remove${name?cap_first}(element);
}