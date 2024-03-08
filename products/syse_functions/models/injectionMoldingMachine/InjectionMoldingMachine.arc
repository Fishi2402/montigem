package injectionMoldingMachine;

component InjectionMoldingMachine {
  port in PlasticGranules inputMaterial;
  port in Electricity power;

  port out Part output;

  InjectionUnit injectionUnit;
  ClampingUnit clampingUnit;
  ElectricityController eController;

  connect inputMaterial -> injectionUnit.granules;
  connect injectionUnit.melt -> clampingUnit.melt;
  connect clampingUnit.output -> output;

  connect power -> eController.input;
  connect eController.injectionUnitInput -> injectionUnit.power;
  connect eController.clampingUnitInput -> clampingUnit.power;
}
