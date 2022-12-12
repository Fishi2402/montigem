<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("sourceName", "targetName")}
${cd4c.method("public Collection<Long> getEffectedGemIds()")}

return Arrays.asList(${sourceName?uncap_first}GemId, ${targetName?uncap_first}GemId,
                      old${sourceName?cap_first}GemId, old${targetName?cap_first}GemId);
