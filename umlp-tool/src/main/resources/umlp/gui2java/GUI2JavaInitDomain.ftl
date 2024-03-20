<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attr", "manager")}
${cd4c.method("protected void init${attr.getName()?cap_first}(${attr.getMCType().printType()} val)")}
this.${attr.getName()} = val;
