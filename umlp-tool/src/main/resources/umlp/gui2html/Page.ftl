<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a html file from the given model -->
${tc.signature("notNestedComponents")}

<#list genHelper.getGUIViewElementParameters(ast) as param>
  <ng-container *ngFor="let c of ${param.getName()}">
    <ng-template [ngTemplateOutlet]="c">
    </ng-template>
  </ng-container>
</#list>

<#list notNestedComponents as component>
  ${tc.include("umlp.gui2html.Component", component)}
</#list>