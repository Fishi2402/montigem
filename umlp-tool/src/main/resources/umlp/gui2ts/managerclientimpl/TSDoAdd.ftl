<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "managerName")}
${cd4c.method("protected void _doAdd${name}(${name} o)")}
  let res: StatusResult = CommandManager.executeCommand(new ${name}_create(o)).asStatusResult();
  // TODO: Error handling
  console.debug("Adding known ${name?uncap_first} " + o.getGemId());
  console.trace();
  this.known${name}s.set(o.getGemId(), o);
  if (!o.containsObserver(this.the${name}CommandObserver)) {
    o.addObserver(this.the${name}CommandObserver);
  }
  this.${managerName?uncap_first}Observers.forEach(obs => obs.notify${name}Added(o));
