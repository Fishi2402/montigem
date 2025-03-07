import {Component} from '@angular/core';
import {TestBidirectiComponent, config} from 'test_assoc/TestBidirectiComponent';
import {Event} from "@umlp/common";
import {RightSide} from 'loadingtests/RightSide';
import {LeftSide} from 'loadingtests/LeftSide';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles
})
export class TestBidirecti extends TestBidirectiComponent {

  public connectToLeft(l: LeftSide, r: RightSide): Event<void> {
    return () => {
      l.setConnectedRight1(r);
    }
  }

  public connectToRight(l: LeftSide, r: RightSide): Event<void> {
    return () => {
      r.setConnectedLeft1(l);
    }
  }

  public disconnectFromLeft(l: LeftSide): Event<void> {
    return () => {
      l.setConnectedRightAbsent1();
    }
  }

  public disconnectFromRight(r: RightSide): Event<void> {
    return () => {
      r.setConnectedLeftAbsent1();
    }
  }
}
