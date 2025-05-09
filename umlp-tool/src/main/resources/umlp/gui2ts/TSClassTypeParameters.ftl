<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTCDClass ast -->
<#if ast.isPresentTypeParameters()>
  <<#list ast.getTypeParameters() as typeParameter>${typeParameter}<#sep>,</#list>>
</#if>
