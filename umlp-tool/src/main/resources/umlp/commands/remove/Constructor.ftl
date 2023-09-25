<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "packageName", "attrName", "attrType")}
${cd4c.constructor("public ${classname}(long gemId, ${attrType} ${attrName}, int indexInList)")}

super("${packageName}.${classname}");
this.gemId = gemId;
this.${attrName} = ${attrName};
this.indexInList = indexInList;
