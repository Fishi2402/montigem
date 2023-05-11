<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("parameters")}
${cd4c.method("public void build()")}
   <#list parameters as parameter>
   if (!this.${parameter}.isBuildFinished() && this.${parameter}.isBuildable()) {
     this.${parameter}.build();
   }
   </#list>