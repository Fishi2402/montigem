<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("parameters")}
${cd4c.method("public void build()")}
     try {
       <#list parameters as parameter>
       if (!this.${parameter}.isBuildFinished() && this.${parameter}.isBuildable()) {
         this.${parameter}.build();
       }
       </#list>
     } catch (IllegalStateException ex) {
       // TODO something goes completely wrong in client (should never happen) or server responds
       //  with an error, handle, but do not stop execution, GUI is never unresponsive
     }