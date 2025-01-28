<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("moduleType", "cdType", "managerName")}
${cd4c.method("protected ${moduleType}Builder _do${moduleType}BuilderFromId(long gemId)")}
return new ${moduleType}4${cdType}DelegatedBuilder(${managerName}.${cdType?uncap_first}Builder());
