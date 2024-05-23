<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ids")}

public constructor() {
  super();
  this._route = inject(ActivatedRoute);
  this._router = inject(Router);

  <#list ids as id>
    if(this._route.snapshot.paramMap.get("${id}") != null) {
      this.${id} = +this._route.snapshot.paramMap.get("${id}");
    }
  </#list>
}
