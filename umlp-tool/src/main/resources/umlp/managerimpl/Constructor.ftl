<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "cdName")}
${cd4c.constructor("public ${name}()")}
${cdName?uncap_first}Observers.add(new ${cdName}EventSendingObserver());
