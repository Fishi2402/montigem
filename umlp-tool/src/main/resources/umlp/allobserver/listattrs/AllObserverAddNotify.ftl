<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "name","classDefiningAttrName", "className","type")}
<#assign attrName = classDefiningAttrName?uncap_first/>
${cd4c.method("public void notifyAdd${name?cap_first}(${classDefiningAttrName} ${attrName}, ${type} arg, int indexInList)")}
  this.consumer.accept(null);
