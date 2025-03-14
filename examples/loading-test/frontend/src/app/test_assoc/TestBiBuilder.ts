import {Component} from '@angular/core';
import {TestBiBuilderComponent, config} from 'test_assoc/TestBiBuilderComponent';
import {Event} from "@umlp/common";
import {LeftSide} from 'loadingtests/LeftSide';
import {LoadingTestsManager} from 'loadingtests/LoadingTestsManager';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles
})
export class TestBiBuilder extends TestBiBuilderComponent {

  public connectNew(l: LeftSide): Event<void> {
    return () => {
      LoadingTestsManager.rightSideBuilder()
          .then(rsb => {
            rsb.label("New single right side").connectedLeft(l).build()
          })
    }
  }

  public connectNewList(l: LeftSide): Event<void> {
    return () => {
      LoadingTestsManager.rightSideBuilder()
          .then(rsb => {
            rsb.label("New right side in list").connectedLeftList(l).build()
          })
    }
  }
}
