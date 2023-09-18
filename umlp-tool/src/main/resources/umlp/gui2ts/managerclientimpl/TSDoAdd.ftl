<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "managerName")}
${cd4c.method("public Promise<Void> _doAdd${name}2(${name} o, NotificationScope scope)")}
  console.debug("Adding known ${name?uncap_first} " + o.getGemId());
  console.trace();
  this.known${name}s.set(o.getGemId(), Promise.resolve(o));
  if (!o.containsObserver(this.the${name}CommandObserver)) {
    o.addObserver(this.the${name}CommandObserver);
  }
  this.${managerName?uncap_first}Observers.forEach(obs => obs.maybeNotify${name}Added(o, scope));

  if(scope.contains(NotificationScope.GLOBAL)){
    return CommandManager.executeCommand(new ${name}_create(o)).then(v => {
      let res = v.asStatusResult();
      // TODO: Error handling

    })
  }else{
    return Promise.resolve();
  }
