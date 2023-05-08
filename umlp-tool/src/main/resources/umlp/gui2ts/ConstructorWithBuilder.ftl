<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("diagrammName", "name", "attributes", "guiDSLPrinter")}
${cd4c.constructor("protected constructor()")}
<#list attributes as attribute>
    this.${attribute.getName()} = ${diagrammName}Manager.${guiDSLPrinter.prettyprint(attribute.getMCType())?uncap_first}();
</#list>
    this.build();