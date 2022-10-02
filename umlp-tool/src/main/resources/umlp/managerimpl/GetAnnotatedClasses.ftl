<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("nameList")}
${cd4c.method("private Set<Class> getAnnotatedClasses()")}

  Set<Class> annotatedClasses = new HashSet<>();
  <#list nameList as name>
    annotatedClasses.add(${name}.class);
  </#list>
  return annotatedClasses;