<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("diagrammName", "name", "attributes", "guiDSLPrinter")}
protected constructor() {
<#list attributes as attribute>
    this.${attribute.getName()} = ${diagrammName}Manager.${guiDSLPrinter.prettyprint(attribute.getMCType())?uncap_first}();
</#list>
    this.build();
}