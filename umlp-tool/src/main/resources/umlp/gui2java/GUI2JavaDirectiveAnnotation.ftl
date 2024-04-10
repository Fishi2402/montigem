<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "isEmptyParamList")}
@Directive(
  cdName = "${cdName}",
  isEmpty = ${isEmptyParamList?c},
  isCreate = false
)
