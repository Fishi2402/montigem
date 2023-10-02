<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("decoratedAST", "glex", "hwPath", "cdGenerator", "schemaGenerator")}

<#-- add extra endpoints -->
<#assign restGetAllDecorator = tc.instantiate("umlp.generator.service.RestGetAllDecorator", [glex])>
${restGetAllDecorator.transform(ast, decoratedAST)}

<#-- Generate default backend -->
${includeArgs("umlp.UMLP2Java", decoratedAST, glex, hwPath, cdGenerator, schemaGenerator)}

<#-- Generate GUI for teaching -->
<#assign uiGenerator = tc.instantiate("umlp.generator.gui.UI4TeachingGenerator", [tc.getGeneratorSetup()])>
${uiGenerator.generate(ast)}
