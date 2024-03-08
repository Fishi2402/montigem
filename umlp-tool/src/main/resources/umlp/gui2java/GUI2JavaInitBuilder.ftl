<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature(attr, manager)}
${cd4c.method("protected void init${attr.getName()?cap_first}()")}
this.${attr.getName()} = ${manager}.${attr.getMCType().printType()?uncap_first}Builder();
