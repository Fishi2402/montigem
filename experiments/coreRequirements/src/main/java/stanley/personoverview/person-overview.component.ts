/* (c) https://github.com/MontiCore/monticore */
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
@Component({
  "selector": "person-overview",
  "templateUrl": "./person-overview.component.html",
  "styles": [
    ":host { display:flex; flex-direction:column; }"
  ],
})
export class PersonOverview implements OnInit {

  @Input()
  protected ps: Person[] = [];

  protected rows: any[] = [];
  protected tableColumns: Col[] = [];

  public constructor() { }

  public ngOnInit() {
    this.setRows(ps);

    const initialTableColumns: Col[] = [];
    initialTableColumns.push(new Col("age"));
    initialTableColumns.push(new Col("nickname"));
    this.setTableColumns(initialTableColumns);
  }

  public getRows(): any[] {
    return this.ps;
  }

  public setRows(rows: any[]): void {
    this.rows = rows;
  }

  public getTableColumns(): Col[] {
    return tableColumns;
  }

  public setTableColumns(value: Col[]): void {
    tableColumns = value;
  }

}
