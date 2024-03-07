<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName", "paramName")}

if(!${paramName}.containsObserver(${attrName})) {
  ${paramName}.addObserver(${attrName});
}
