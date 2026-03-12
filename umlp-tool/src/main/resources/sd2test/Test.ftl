<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("processor", "defaultMS", "initStmts", "preInteractionStmts", "triggerStmts")}
${cd4c.method("void testSequence()")}
  var ${processor} = new InteractionProcessor(${defaultMS});

  //init
<#list initStmts as stmt>
  ${stmt};
</#list>

  //pre interaction statements
<#list preInteractionStmts as stmt>
  ${stmt};
</#list>

  //activate processor
  MethodMonitor.setActiveProcessor(${processor});

  //trigger
<#list triggerStmts as stmt>
  ${stmt};
</#list>

