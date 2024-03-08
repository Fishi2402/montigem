<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attr")}
<#assign name = attr.getName()>
${cd4c.method("public void build${name?cap_first}()")}

try {
  if (!this.${name}.isBuildFinished() && this.${name}.isBuildable()) {
    this.${name}.build();
  }
} catch (IllegalStateException ex) {
  this.build${name?cap_first}Error(ex);
}