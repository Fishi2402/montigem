/* (c) https://github.com/MontiCore/monticore */

import {config, MultiProcessingStationComponent} from '../../../../../../build/gui2java2ts/mc/fenix/fischertechnik/MultiProcessingStationComponent';
import {Component, HostBinding, Input} from '@angular/core';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: "MultiProcessingStation.html",
})
export class MultiProcessingStation extends MultiProcessingStationComponent {

  // --- Outputs ---

  @Input()
  @HostBinding("style.--refSwitchTurnTableAtVacuumOpacity")
  refSwitchTurnTableAtVacuumOpacity: number;

  override set refSwitchTurnTableAtVacuum(value: boolean) {
    super.refSwitchTurnTableAtVacuum = value;
    this.refSwitchTurnTableAtVacuumOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--refSwitchTurnTableAtBeltOpacity")
  refSwitchTurnTableAtBeltOpacity: number;

  override set refSwitchTurnTableAtBelt(value: boolean) {
    super.refSwitchTurnTableAtBelt = value;
    this.refSwitchTurnTableAtBeltOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--refSwitchTurnTableAtSawOpacity")
  refSwitchTurnTableAtSawOpacity: number;

  override set refSwitchTurnTableAtSaw(value: boolean) {
    super.refSwitchTurnTableAtSaw = value;
    this.refSwitchTurnTableAtSawOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--lightBarrierConveyorEndOpacity")
  lightBarrierConveyorEndOpacity: number;

  override set lightBarrierConveyorEnd(value: boolean) {
    super.lightBarrierConveyorEnd = value;
    this.lightBarrierConveyorEndOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--refSwitchFeederInsideOpacity")
  refSwitchFeederInsideOpacity: number;

  override set refSwitchFeederInside(value: boolean) {
    super.refSwitchFeederInside = value;
    this.refSwitchFeederInsideOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--refSwitchFeederOutsideOpacity")
  refSwitchFeederOutsideOpacity: number;

  override set refSwitchFeederOutside(value: boolean) {
    super.refSwitchFeederOutside = value;
    this.refSwitchFeederOutsideOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--refSwitchVacuumAtTurnTableOpacity")
  refSwitchVacuumAtTurnTableOpacity: number;

  override set refSwitchVacuumAtTurnTable(value: boolean) {
    super.refSwitchVacuumAtTurnTable = value;
    this.refSwitchVacuumAtTurnTableOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--refSwitchVacuumAtOvenOpacity")
  refSwitchVacuumAtOvenOpacity: number;

  override set refSwitchVacuumAtOven(value: boolean) {
    super.refSwitchVacuumAtOven = value;
    this.refSwitchVacuumAtOvenOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--lightBarrierOvenOpacity")
  lightBarrierOvenOpacity: number;

  override set lightBarrierOven(value: boolean) {
    super.lightBarrierOven = value;
    this.lightBarrierOvenOpacity = value ? 1 : 0;
  }

  // --- Inputs ---

  @Input()
  @HostBinding("style.--moveTurnTableClockwiseOpacity")
  moveTurnTableClockwiseOpacity: number;

  override set moveTurnTableClockwise(value: boolean) {
    super.moveTurnTableClockwise = value;
    this.moveTurnTableClockwiseOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--moveTurnTableCounterclockwiseOpacity")
  moveTurnTableCounterclockwiseOpacity: number;

  override set moveTurnTableCounterclockwise(value: boolean) {
    super.moveTurnTableCounterclockwise = value;
    this.moveTurnTableCounterclockwiseOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--moveConveyorForwardOpacity")
  moveConveyorForwardOpacity: number;

  override set moveConveyorForward(value: boolean) {
    super.moveConveyorForward = value;
    this.moveConveyorForwardOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--enableSawOpacity")
  enableSawOpacity: number;

  override set enableSaw(value: boolean) {
    super.enableSaw = value;
    this.enableSawOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--moveOvenFeederRetractOpacity")
  moveOvenFeederRetractOpacity: number;

  override set moveOvenFeederRetract(value: boolean) {
    super.moveOvenFeederRetract = value;
    this.moveOvenFeederRetractOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--moveOvenFeederExtendOpacity")
  moveOvenFeederExtendOpacity: number;

  override set moveOvenFeederExtend(value: boolean) {
    super.moveOvenFeederExtend = value;
    this.moveOvenFeederExtendOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--moveVacuumToOvenOpacity")
  moveVacuumToOvenOpacity: number;

  override set moveVacuumToOven(value: boolean) {
    super.moveVacuumToOven = value;
    this.moveVacuumToOvenOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--moveVacuumToTurnTableOpacity")
  moveVacuumToTurnTableOpacity: number;

  override set moveVacuumToTurnTable(value: boolean) {
    super.moveVacuumToTurnTable = value;
    this.moveVacuumToTurnTableOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--enableOvenLightOpacity")
  enableOvenLightOpacity: number;

  override set enableOvenLight(value: boolean) {
    super.enableOvenLight = value;
    this.enableOvenLightOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--enableCompressorOpacity")
  enableCompressorOpacity: number;

  override set enableCompressor(value: boolean) {
    super.enableCompressor = value;
    this.enableCompressorOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--enableValveVacuumOpacity")
  enableValveVacuumOpacity: number;

  override set enableValveVacuum(value: boolean) {
    super.enableValveVacuum = value;
    this.enableValveVacuumOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--enableValveMoveVacuumOpacity")
  enableValveMoveVacuumOpacity: number;

  override set enableValveMoveVacuum(value: boolean) {
    super.enableValveMoveVacuum = value;
    this.enableValveMoveVacuumOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--enableValveMoveOvenDoorOpacity")
  enableValveMoveOvenDoorOpacity: number;

  override set enableValveMoveOvenDoor(value: boolean) {
    super.enableValveMoveOvenDoor = value;
    this.enableValveMoveOvenDoorOpacity = value ? 1 : 0;
  }

  @Input()
  @HostBinding("style.--enableValveMoveFeederOpacity")
  enableValveMoveFeederOpacity: number;

  override set enableValveMoveFeeder(value: boolean) {
    super.enableValveMoveFeeder = value;
    this.enableValveMoveFeederOpacity = value ? 1 : 0;
  }
}
