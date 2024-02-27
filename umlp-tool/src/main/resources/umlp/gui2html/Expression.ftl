<#-- (c) https://github.com/MontiCore/monticore -->
<#assign genHelper = glex.getGlobalVar("genHelper")>

<ng-container *ngTemplateOutlet="${genHelper.print(ast.getExpression())}"></ng-container>
