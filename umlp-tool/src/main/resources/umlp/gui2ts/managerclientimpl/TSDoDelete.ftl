<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "managerName")}
${cd4c.method("public Promise<Boolean> _doDelete${name}2(Long gemId, NotificationScope scope)")}

let res: Promise<Boolean>
if(scope.contains(NotificationScope.GLOBAL)){
  res = CommandManager.executeCommand(new ${name}_delete(gemId)).then(v => {
    let status = v.asStatusResult();
    return status.wasSuccessful();
  });
} else {
  res = Promise.resolve(true);
}

return res.then(b => {
  if(b){
    // TODO: error handling
    this.known${name}s.delete(gemId);
    if (this.loaded${name}List) {
      this.loaded${name}List.then(tmp_$ => {
        tmp_$.removeIdSure(gemId);
      });
    }
    this.${managerName?uncap_first}Observers.forEach(obs => obs.maybeNotify${name}Deleted(gemId, scope));
    return true;
  }else{
    return false;
  }
});
