<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("initializationTypeName")}
${cd4c.method("void maybeNotifyBuild(${initializationTypeName} ${initializationTypeName?uncap_first}, NotificationScope scope)")}

if(this.getScope().contains(scope)) {
    this.notifyBuild(${initializationTypeName?uncap_first});
}
