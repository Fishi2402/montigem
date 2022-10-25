<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("diagrammName", "name", "attributes", "guiDSLPrinter")}
${cd4c.constructor("protected ${name}()")}
<#list attributes as attribute>
    this.${attribute.getName()} = ${diagrammName}Manager.${guiDSLPrinter.prettyprint(attribute.getMCType())};
</#list>
    this.build();

    init();