<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "naiveAttributeName", "generatedErrorCode")}
    if (this.isPresent${naiveAttributeName}()) {
        return this.${attribute.getName()};
    }
    console.error("0xB7003${generatedErrorCode} get for ${naiveAttributeName} can't return a value. Attribute is empty.");
    return null;
