<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  The build method is generated to allow object creation.

-->
${tc.signature("class", "paramList", "allAssociations")}

<#assign assocUtil = tc.instantiate("umlp.generator.builder.CDAssociationUtil")>
<#assign transformationUtils = tc.instantiate("umlp.generator.builder.TransformationUtils")>
<#assign typeHelper = tc.instantiate("umlp.generator.builder.TypeHelper")>

<#if class.isIsAbstract()>
throw new de.monticore.montigem.rte.be.error.NotImplementedException("${transformationUtils
.getErrorCode()}: Cannot create ${class.name} object, because it is abstract.");
<#else>
<#assign constructorArguments = []>

<#list paramList as attr>
    <#assign constructorArguments = constructorArguments + ["${attr.getName()}"]>
</#list>

// Create the actual object
${class.getName()} newObj = new ${class.getName()}( ${constructorArguments?join(",")} );

// Set the id
newObj.setUmlpId(umlpId);

if (addToBidirectionalAssociation) {
<#list allAssociations as assoc>
    <#if transformationUtils.isBidirectional(assoc)>
        <#if assocUtil.isSourceOne(assoc) || assocUtil.isSourceOptional(assoc)>
            <#if assocUtil.isOne(assoc)>
                ${assocUtil.associationToCDParameter(assoc).getName()}.rawSet${(assocUtil.getOppositeAssociationName(assoc))?cap_first}(newObj);
            <#elseif assocUtil.isOptional(assoc)>
                ${assocUtil.associationToCDParameter(assoc).getName()}.ifPresent(o -> o.rawSet${(assocUtil.getOppositeAssociationName(assoc))?cap_first}(newObj));
            <#elseif assocUtil.isMultiple(assoc)>
              for (${typeHelper.getFirstTypeArgumentOfGeneric(assocUtil.getAssociationType(assoc))} o : this.${assocUtil.getAssociationName(assoc)}) {
              o.set${(assocUtil.getOppositeAssociationName(assoc))?cap_first}(newObj);
              }
            </#if>
        <#elseif assocUtil.isSourceMultiple(assoc)>
            <#if assocUtil.isOne(assoc)>
                ${assocUtil.associationToCDParameter(assoc).getName()}.add${(assocUtil.getOppositeAssociationName(assoc))?cap_first}(newObj);
            <#elseif assocUtil.isOptional(assoc)>
                ${assocUtil.associationToCDParameter(assoc).getName()}.ifPresent(o -> o.add${(assocUtil.getOppositeAssociationName(assoc))?cap_first}(newObj));
            <#elseif assocUtil.isMultiple(assoc)>
              for (${typeHelper.getFirstTypeArgumentOfGeneric(assocUtil.getAssociationType(assoc))} o : this.${assocUtil.getAssociationName(assoc)}) {
              o.add${(assocUtil.getOppositeAssociationName(assoc))?cap_first}(newObj);
              }
            </#if>
        </#if>
    </#if>
</#list>
}

  // Result<Boolean, String> validationMessages = ${class.getName()}Validator.getValidator().getValidationErrors(newObj);
  // validationMessages.throwOnError(validationMessages.isErr() ? new ValidationException(validationMessages.getErr()) : null);

  return newObj;
</#if>
