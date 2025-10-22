<#-- (c) https://github.com/MontiCore/monticore -->
<#-- GUIDocArtifact doc, PrintUtil util -->
${tc.signature("doc", "util")}
<#assign gui = doc.getComponentAST()>
<#assign examples = doc.getExampleList()>
<#assign types = doc.getCDList()>

<!-- (c) https://github.com/MontiCore/monticore -->

${util.printComment(gui.getGUIComponentDeclaration().get_PreCommentList())}

<#if examples?has_content>
## Usage Example<#if examples?size &gt; 1>s</#if>

<#list examples as example>
<#if doc.hasExampleModel(example)>
```ts
${doc.getExampleModel(example)}<#t>
```
<#if doc.hasExampleImage(example)>
<img src='${util.printRelativePath(doc.getOutputFile(), doc.getExampleImage(example))}' alt='${example}' width='50%'>
</#if>
</#if>

<#if doc.hasExampleCode(example)>
TypeScript implementation:
```ts
${doc.getExampleCode(example)}
```
</#if>
</#list>
</#if>

## GUI Model

```ts
${doc.getComponentModel()}<#t>
```

| Name | Type | Description | Default value |
| --- | --- | --- | --- |
<#list gui.getGUIComponentDeclaration().getGUIParamList() as param>
| `${param.getName()}` <#rt>
| `<#if param.isReferenceParam()>?</#if>${param.getMCType().printType()}`<#rt>
| ${util.printComment(param.get_PreCommentList())} <#rt>
| <#if param.isPresentDefault()>`${util.print(param.getDefault())}`</#if> |
</#list>

<#if types?has_content>
## Types

<#list types as type>
### `${type}`
${util.printPath(doc.getCDAST(type).getMCPackageDeclaration().getMCQualifiedName() + "." + doc.getCDAST(type).getCDDefinition().getName())}.cd))

```cd
${doc.getCDModel(type)}<#t>
```

<#list doc.getCDAST(type).getCDDefinition().getCDClassesList() as clazz>
#### `${clazz.getName()}`
| Name | Type | Description | Default value |
| --- | --- | --- | --- |
<#list clazz.getCDAttributeList() as attr>
| `${attr.getName()}` <#rt>
| `${attr.getMCType().printType()}` <#rt>
| ${util.printComment(attr.get_PreCommentList())} <#rt>
| <#if attr.isPresentInitial()>`${util.print(attr.getInitial())}`</#if> |
</#list>
</#list>
</#list>
</#if>