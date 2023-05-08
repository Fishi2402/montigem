<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName", "sourceName")}

${cd4c.method("public void INTERNAL_add${name?cap_first}Id(int idx, long gemID, NotificationScope scope)")}
_${name}._internal_add(idx, Arrays.asList(gemId));
${sourceName?uncap_first}Observers.forEach(obs -> obs.maybeNotifyAdd${name?cap_first}((${sourceName}) this, gemId, idx, scope));
