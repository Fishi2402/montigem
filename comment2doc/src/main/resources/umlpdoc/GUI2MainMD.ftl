<#-- (c) https://github.com/MontiCore/monticore -->
<#-- GUIDocArtifact doc, PrintUtil util -->
${tc.signature("sublibs", "outputPath", "util")}

## Introduction

**GUI Components** are the elements of web pages.
This document lists MontiGem GUI library components.

Each component description includes:

- GUI component *usage examples*
- GUI component *signature*, i.e. component name and its input *parameters*
- A description of each *input parameter*, including the types and default values

The GUI component library may be used as is to model web pages, or can be
composed to create more complex reusable GUI components. For example, layout
components can be combined with input components to create forms, or with charts
to create dashboards.

<#list sublibs?keys as sublib>
## ${sublib?cap_first} components
  <#list sublibs[sublib] as doc>
    <#assign gui = doc.getComponentAST()>
    <#assign examples = doc.getExampleList()>

### [${gui.getComponentName()}](${util.printRelativePath(outputPath, doc.getOutputFile())})

${util.printComment(gui.getGUIComponentDeclaration().get_PreCommentList())}

<#if examples?size &gt; 0 && doc.hasExampleImage(examples?first)>
<img src='${util.printRelativePath(outputPath, doc.getExampleImage(examples?first))}' alt='${examples?first}' width='50%'>
</#if>

---
  </#list>
</#list>