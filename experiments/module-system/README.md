# Module system

`module-definition` defines a module, which includes
1. (gen) A new decorator to transform the input cd: `SomeDecorator`
2. (gen) A new generator mock: `SomeGenerator`
3. (gen) Configuration templates to embed the generator and decorator into the backend generation task
4. (gen) A CD model, that can be imported `SomeModel.umlp`
5. (gen) Two GUI models, that can be imported or used directly: `SomePage.gui`, `SomeComponent.gui`
6. (gen) A file with metadata: `SomeModule.metadata.json`
6. (compile) A class that uses the TOP-mechansim: `Foo.java`
7. (compile) The classes generated from the CD model `SomeModel.umlp`

All items marked with (gen) need to be available during generation time and are therefore packed into
a jar with the classifier `module-generator-time`. Classes that should be used during generation need
to be placed at [moduleGenTime](module-definition%2Fsrc%2FmoduleGenTime).
This jar is then added to the classpath of the generator, making all classes and templates available.

All items marked with (compile) need to be available during Java compilation in the consuming project
and are packed into the normal jar without a classifier.

`module-usage` imports and uses the defined module and contains the needed Gradle configuration to
adapt the umlp tasks.

# Configuration
All modules need to provide a file `${name}.metadata.json`, which has info on what the module
contains and how it should be used. This file defines a config template per task that needs to be
adapted. Here the backend and frontend tasks are configured with 
[BackendConfig.ftl](module-definition%2Fsrc%2FmoduleGenTime%2Fresources%2Fsome_module%2FBackendConfig.ftl)
and [FrontendConfig.ftl](module-definition%2Fsrc%2FmoduleGenTime%2Fresources%2Fsome_module%2FFrontendConfig.ftl)
TODO: CDMerge

These config templates are called at the beginning of the generation process and define other templates
that should be executed at different hook points of the generator.
`BackendConfig.ftl` hooks the decorator into `umlp.generator.hook.BeforeTransform` and the generator into
`umlp.generator.hook.AfterTransform`.
Only `glex.addAfterTemplate` must be used, which allows us to use multiple templates in the same
hookpoint, enabling the use of multiple modules. 
