${tc.signature("decoratedAST", "glex", "hwPath", "cdGenerator", "schemaGenerator", "beDecorator", "commandDecorator", "requestDecorator", "topDecorator")}
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController"-->
<#-- @ftlvariable name="glex" type="de.monticore.generating.templateengine.GlobalExtensionManagement"-->
<#-- @ftlvariable name="decoratedAST" type="de.monticore.cdbasis._ast.ASTCDCompilationUnit"-->

<#assign decorator = tc.instantiate("comment_module.UMLPShellAPIImportDecorator", [glex]) />
${decorator.transform(ast, decoratedAST)}
${info("After decorator: ShellAPIImportDecorator", "Comment module")}
