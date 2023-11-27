<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type1", "type2", "command")}
${cd4c.method("public boolean checkAccessPolicy()")}

${type1}AccessPolicy ap1 = new ${type1}AccessPolicy();
${type2}AccessPolicy ap2 = new ${type2}AccessPolicy();
return ap1.check${command}Permission(this) && ap2.check${command}Permission(this);
