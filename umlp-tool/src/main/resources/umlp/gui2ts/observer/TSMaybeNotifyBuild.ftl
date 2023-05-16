<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className")}
${cd4c.method("void maybeNotifyBuild(${className} ${className?uncap_first}, NotificationScope scope)")}

if(this.getScope().contains(scope)) {
    this.notifyBuild(${className?uncap_first});
}