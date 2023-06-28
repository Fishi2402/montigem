<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("htmlPath", "scssPaths")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
@Component({
  selector: '${genHelper.getComponentNameFromCamelCaseName(ast.getName())}',
  templateUrl: '${htmlPath}',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: [<#list scssPaths as path>'${path}',</#list>]
})
