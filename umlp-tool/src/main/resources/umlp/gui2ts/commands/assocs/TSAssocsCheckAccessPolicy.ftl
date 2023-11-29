<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type1", "type2", "command")}
${cd4c.method("public boolean checkAccessPolicy()")}

let ap1: ${type1}AccessPolicy = new ${type1}AccessPolicy();
let ap2: ${type2}AccessPolicy = new ${type2}AccessPolicy();
return ap1.check${command}Permission(this) && ap2.check${command}Permission(this);
