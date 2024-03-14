<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attr", "manager")}
${cd4c.method("protected void init${attr.getName()?cap_first}()")}
${manager}.get${attr.getMCType().printType()}Sure(this.${attr.getName()}RoutingId).then(v => this.${attr.getName()} = v);
