<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}

${cd4c.method("public void remove${name?cap_first}ByIdSure(long gemId, NotificationScope scope)")}
int idx = _${name}.indexOfId(gemId);
if(idx == -1){
  Log.error("Can not find gemId " + gemId + " in _${name}");
} else {
  _internal_remove${name?cap_first}(Collections.singleton(idx), scope);
}
