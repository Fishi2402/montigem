<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("htmlPath", "scssPaths", "selector")}
@Component({
  selector: '${selector}',
  templateUrl: '${htmlPath}',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: [<#list scssPaths as path>'${path}',</#list>]
})
