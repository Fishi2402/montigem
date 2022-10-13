<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "packageName", "targetName")}
${cd4c.constructor("public ${classname}(long gemId, long ${targetName}, int indexInList)")}

super("${packageName}.${classname}");
this.gemId = gemId;
this.${targetName} = ${targetName};
this.indexInList = indexInList;
