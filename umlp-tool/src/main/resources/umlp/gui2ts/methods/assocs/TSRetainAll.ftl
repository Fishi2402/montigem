<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName")}

${cd4c.method("public void retainAll${name?cap_first}s(Collection<${targetName}> o)")}
if (o == null) {
  console.error("0x50004 Null argument not allowed here");
}

for (const s of o) {
  this.remove${name?cap_first}2(s);
}

