<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type")}
<#assign mcType = type.printType()>
<#assign javaType = cdPrinter.printType(type)>
CommandManager.executeCommand(new ${javaType}_subscribeManagerEvents());