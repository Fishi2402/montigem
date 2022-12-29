/* (c) https://github.com/MontiCore/monticore */
// GEN-P (generated from PersonStatistics.gui or
// translated from PersonStatisticsGUI.java)

/* This is the code that is generated from model (or translated from
   generated Java). It implements GUI page logic using TypeScript.
*/

/*
 * Import RTE components and symbols from framework
 */
// import {...} from '...';

// Obligatory decorator for components
@Component({
  "selector": "person-statistics",
  "templateUrl": "./person-statistics.component.html",
  "styles": [
    ":host { display:flex; flex-direction:column; }"
  ],
})
export class PersonStatistics extends PersonStatisticsTOP implements OnInit {

  public constructor() {
    super();
  }

  public setPieData(ps: Person[]): void {
    result: PieData[] = [];
    // Pie data with description. Description is a unique identifier for a pie slice
    // initial value for both is 0
    PieData slice1 = new PieData("younger than 20", 0);
    result.push(slice1);

    PieData slice2 = new PieData("older than 20 or 20", 0);
    result.push(slice2);
    // count persons younger than 20 and rest
    for (let p of ps) {
      if (p.getAge() < 20) {
        slice1.setValue(slice1.getValue() + 1);
      } else {
        slice2.setValue(slice2.getValue() + 1);
      }
    }
  }

}
