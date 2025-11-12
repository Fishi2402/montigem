<#-- (c) https://github.com/MontiCore/monticore -->

<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign extraArgs = genHelper.findIterationItemSymbols(ast.getEnclosingScope())>
<#assign itemName = genHelper.getShortIteratorItemName(ast)>
<#assign iterable = genHelper.getShortIteratorIterable(ast)>
<#assign iterableStr = genHelper.printShortIteratorIterable(ast)>
<#assign iterableGetter = genHelper.getIterableGetter(ast)>
<#assign counterName = genHelper.getIteratorCounterName(ast)>

<#if genHelper.isViewElementsCollection(iterable)><#t>
  <ng-container *ngFor='let ${itemName} of ${iterableStr}; index as ${counterName}'>
<#else><#t>
  <ng-container *ngFor='let ${itemName} of ${iterableGetter}; index as ${counterName}'>
</#if>
  ${tc.include("umlp.gui2html.Block", ast.getGUIBlock())}
</ng-container>
