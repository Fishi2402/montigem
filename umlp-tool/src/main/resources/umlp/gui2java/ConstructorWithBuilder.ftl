<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("diagrammName", "name", "attributes", "guiDSLPrinter")}
${cd4c.constructor("protected ${name}()")}
<#list attributes as attribute>
  <#-- TODO: Thix GemList check is uuugly - determine how to make it properly   -->
  <#if attribute.getMCType().printType()?starts_with("GemList") &&  attribute.getMCType().getClass().getName()=="de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType"
  && attribute.getMCType().getMCTypeArgumentList()?size == 1> <!-- The type is GemList<Type> -->
    // Workaround used to access specific GemList using manager
    this.${attribute.getName()} = ${diagrammName}Manager.get${guiDSLPrinter.prettyprint(attribute.getMCType().getMCTypeArgumentList()?first)}List();
  <#else>
    this.${attribute.getName()} = ${diagrammName}Manager.${guiDSLPrinter.prettyprint(attribute.getMCType())?uncap_first}();
  </#if>
</#list>
    init();

    this.build();
