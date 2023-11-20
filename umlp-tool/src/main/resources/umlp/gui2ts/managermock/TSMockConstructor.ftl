<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.constructor("public ${name}Mock()")}
${name?uncap_first}Observers.add(new ${name}EventSendingObserver());
