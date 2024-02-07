<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a html file from the given model -->
${tc.signature("componentName")}
<#assign genHelper = glex.getGlobalVar("genHelper")>

<ng-container *ngTemplateOutlet="${componentName}"></ng-container>
