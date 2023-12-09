<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("htmlPath", "scssPaths", "selector","configPath")}
import {config} from '${configPath}'

<#assign genHelper = glex.getGlobalVar("genHelper")>
@Component({
standalone: true,
imports: [
... config.imports,
],
selector: '${selector}',
templateUrl: '${htmlPath}',
styles: [":host{ display: flex; flex-direction: column }"],
styleUrls: [<#list scssPaths as path>'${path}',</#list>]
})
