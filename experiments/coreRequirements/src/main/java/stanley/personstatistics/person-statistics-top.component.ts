// GEN-P (generated from EditierePersonCars.gui or
// translated from EditierePrsonCarsGUI.java)

/* This is the code that is generated from model (or translated from
   generated Java). It implements GUI page logic using TypeScript.
*/

/*
 * Import RTE components and symbols from framework
 */
// import {...} from '...';

// Obligatory decorator for components
@Directive()
export class PersonStatisticsTOP implements OnInit {

  protected ps: Person[] = [];
  protected pieData: PieData[] = [];

  public constructor() { }

  public abstract setPieData(pieData: Person[]): void;

  public getPieData(): PieData[] {
    return this.pieData;
  }

  public getData(ps: Person[]): PieData[] {
    return this.getPieData();
  }

}
