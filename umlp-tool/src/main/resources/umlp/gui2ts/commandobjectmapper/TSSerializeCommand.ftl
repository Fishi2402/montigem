<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("superCdDeSers")}
${cd4c.method("protected String _serializeCommand(Command<any> command)")}

return this.commandDeSer.serialize(command)
  <#list superCdDeSers as tmp>
    ?? this.${tmp}.serialize(command)
  </#list>
;
