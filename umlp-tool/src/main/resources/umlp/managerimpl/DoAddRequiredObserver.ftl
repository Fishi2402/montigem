<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "name")}
${cd4c.method("protected void _doAddRequiredObserver${name}(${name} h)")}
  if(!h.containsObserver(the${name}DbObserver)){
    h.addObserver(the${name}DbObserver);
  }
  if(!h.containsObserver(the${name}CommandSendingObserver)){
    h.addObserver(the${name}CommandSendingObserver);
  }

