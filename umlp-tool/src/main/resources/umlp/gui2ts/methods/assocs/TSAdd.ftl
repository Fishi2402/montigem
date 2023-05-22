<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName")}

${cd4c.method("public void add${name?cap_first}(${targetName} o, NotificationScope scope)")}

this.add${name?cap_first}1(this._${name}.length, o, scope);