<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName")}

${cd4c.method("public void retainAll${name?cap_first}s(Collection<${targetName}> o)")}
if (o == null) {
  throw new IllegalArgumentException("Null argument not allowed here");
}

Sets.difference(new HashSet<>(this._${name}), new HashSet<>(o)).forEach((s) -> {
  this.remove${name?cap_first}(s);
});

