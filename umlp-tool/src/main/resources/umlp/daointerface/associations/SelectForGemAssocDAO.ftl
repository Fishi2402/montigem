<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("startingSide", "endingSide")}
<#assign methodName = endingSide?cap_first+"IdFor"+startingSide?cap_first>
${cd4c.method("public abstract Optional<Long> select${methodName}(long ${startingSide}GemId)")}
