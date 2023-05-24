<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName", "sourceName")}

${cd4c.method("public void INTERNAL_add${name?cap_first}Id(int idx, long gemID, NotificationScope scope)")}
this._${name}._internal_add(idx, [gemID]);
this.${sourceName?uncap_first}Observers.forEach(obs => obs.maybeNotifyAdd${name?cap_first}(<${sourceName}> this, gemID, idx, scope));
