<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "assoc")}
<#assign mcType = type.printType()>
<#assign javaType = cdPrinter.printType(type)>
<#assign arg = "val_$">
<#assign arg2 = "val_$2">
<#assign arg3 = "val_$3">

${cd4c.method("public void notifyAdd${assoc?cap_first}(${mcType} ${arg}, long ${arg2}, int ${arg3})")}
/*
${arg}._internal_get${assoc?cap_first}GemList().loadSingleObject(${arg2});
*/