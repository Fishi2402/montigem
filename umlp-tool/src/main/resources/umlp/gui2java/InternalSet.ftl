<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type")}
${cd4c.method("protected void set${name?cap_first}_internal(${type} v)")}
  this.${name} = v;
