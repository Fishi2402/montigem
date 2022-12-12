<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("sourceName", "targetName")}
${cd4c.method("public Collection<Long> getEffectedGemIds()")}

return Arrays.asList(${sourceName}GemId, ${targetName}GemId);
