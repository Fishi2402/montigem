<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("oneSideName", "otherSide")}
<#assign methodName = otherSide?cap_first+"IdsFor"+oneSideName?cap_first>
${cd4c.method("public abstract List<Long> select${methodName}(long ${oneSideName}GemId)")}
