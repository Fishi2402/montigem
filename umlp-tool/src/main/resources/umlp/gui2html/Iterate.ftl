<#-- (c) https://github.com/MontiCore/monticore -->

<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign extraArgs = genHelper.findIterationItemSymbols(ast.getEnclosingScope())>
<#assign itemName = genHelper.getShortIteratorItemName(ast)>
<#assign iterable = genHelper.getShortIteratorIterable(ast)>
<#assign counterName = genHelper.getIteratorCounterName(ast)>

<ng-container *ngFor='let ${itemName} of ${iterable}; index as ${counterName}'>
  ${tc.include("umlp.gui2html.Block", ast.getGUIBlock())}
</ng-container>
