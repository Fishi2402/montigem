import { Component, Input } from "@angular/core";
import { ChartDataset, ChartOptions } from "chart.js";
import { autoColors } from "../colors";
import { GemSDNshort, GemSunburstData, GemSunburstEntry } from "./gem-sunburst-chart-types/gem-sunburst-chart-types.types-gen";
import { gemInstanceOfSDNshort, gemSdnShortToSDN } from "./gem-sunburst-chart.types";
import { GemSunburstChart as GemSunburstChartTOP } from "./gem-sunburst-chart/gem-sunburst-chart.component-gen";

@Component({
  selector: "gem-sunburst-chart",
  templateUrl: "./gem-sunburst-chart.component.html",
  styleUrls: ["./gem-sunburst-chart.component.scss"],
})

export class GemSunburstChart extends GemSunburstChartTOP {

  public newData: GemSunburstData;

  // Placeholder values
  public entry: GemSunburstEntry = {
    label: "",
    value: 0,
  }
  public subEntries: GemSunburstData[] = [];

  // Standard value scale
  public scaler = 1;

  // Standard values for legend are empty
  public legendColors: string[] = [];
  public legendPosition: string;
  public legendLabels = []

  /**variable that enables or disables the legend */
  public displayLegend = true;

  public errorMessage: String = "";

  // Standard options for ng2-charts
  public options: ChartOptions<'doughnut'> = {
    responsive: true,
    maintainAspectRatio: true,
  };

  private userColors = [];

  public colorsD = [{ backgroundColor: ["#eeeeee"], }];

  public hasData: boolean = false;

  private ignoreErrorInput: boolean = false;

  private depth = 0;

  public labels: string[] = [''];
  public dataSets: ChartDataset<'doughnut'>[] = [];

  public getColors() {
    return autoColors;
  }

  // User can decide if charts in which parent Nodes can have a higher value than the sum of the values of all subEntries are allowed
  @Input()
  public set ignoreError(iEI: boolean) {
    if (iEI != null) {
      this.ignoreErrorInput = iEI;
      // update hasData if there is data, because hasData is set to false when the error is reported
      if (this.newData != null) {
        this.hasData = true;
        // If newData equals a placeholder set in set data instead of real data update hasData to false
        if (this.newData.subEntries == null
          || (this.newData.subEntries.length == 0
            && this.newData.entry.label == ""
            && this.newData.entry.value == 0)) {
          this.hasData = false;
        }
        // call updateData to finish data calculations that were interrupted by hasData == false
        this.updateData();
      }
    }
  }

  // User can have own colors as input
  @Input()
  public set colors(colorArray: string[]) {
    if (colorArray != null) {
      // Colors have to be reversed in calculations
      this.userColors = colorArray.reverse();
    }
  }

  // Input of data; starts calculation for the whole chart component
  @Input()
  public set data(data: any) {
    if (data != null) {
      // Initialize newData as placeholder
      this.newData = { entry: this.entry, subEntries: this.subEntries };
      // Check if data is an instance of GemSDNshort
      if (gemInstanceOfSDNshort(data)) {
        // Set newData as the GemSunburstData that is equal to data in GemSDNshort notation
        this.newData = gemSdnShortToSDN(data as GemSDNshort);
      }
      else {
        // Set newData as data which is in GemSunburstData notation
        this.newData = data as GemSunburstData;
      }
      // Update placeholders
      this.entry = this.newData.entry;
      this.subEntries = this.newData.subEntries;

      // There is data
      this.hasData = true;
      // If data has no subEntries or is just the placeholder -> No data for chart
      if (this.newData.subEntries == null
        || (this.newData.subEntries.length == 0
          && this.newData.entry.label == ""
          && this.newData.entry.value == 0)) {
        this.hasData = false;
      }

      // Start calculations for component
      this.updateData();
    }
  }

  // Comprises calculations of this component
  updateData() {
    // If sums get ignored
    if (this.ignoreErrorInput) {
      // Count the values of the subEntries and update the total value of the parentNode accordingly
      let total = 0;
      for (let i = 0; this.newData.subEntries != null && i < this.newData.subEntries.length; i++) {
        total += this.newData.subEntries[i].entry.value;
      }
      this.newData.entry.value = total;
    }
    setTimeout(() => {
      // Check if data has right syntax
      this.check(this.newData);
      // If there is data
      if (this.hasData) {
        // Calculate depth of data tree
        this.depth = this.getDepth(this.newData);
        // Start calculation of chart entrys and labels
        this.dataNodesToChart(this.newData);
        // Set ng2-charts options
        this.options = { cutout: 0, plugins: { legend: { display: false, } } };
      }
    }, 0)
  }


  // Check the data for correct format
  public check(node: GemSunburstData): void {
    // If data has no subEntries or hasData is already false there is nothing to check
    if (this.hasData && node.subEntries != null && node.subEntries.length > 0) {
      // cur is value of current node
      let cur = node.entry.value;
      for (let i = 0; i < node.subEntries.length; i++) {
        // Substract value of every subNode from cur
        cur -= node.subEntries[i].entry.value;
        // Go into every subNode of cur to check it recursively
        this.check(node.subEntries[i]);
      }
      // If values of subEntries does not have to sum up to value of parentNode
      if (this.ignoreErrorInput) {
        // If there is a part of the parentNode that is not filled by the subEntries
        if (cur > 0) {
          // Add empty node that fills the gap
          node.subEntries.push({ entry: { label: "", value: cur, } });
        }
        // If the sum of the values of the subEntries is higher than the parent node's value -> Error and has no Data 
        if (cur < 0) {
          this.errorMessage = `
            Data doesn't sum up to the total value of a node.
            (subEntries of: ${node.entry.label}, ${node.entry.value})
          `;
          this.hasData = false;
        }
      }
      // If values of subEntries have to sum up to value of parentNode
      else {
        // If the sum of the values of the subEntries does not equal the parent node's value -> Error and has no Data 
        if (cur !== 0) {
          this.errorMessage = `
            Data doesn't sum up to the total value of a node.
            (subEntries of: ${node.entry.label}, ${node.entry.value}).
            Option: "[ignoreError] = true" if this should be ignored.
          `;
          this.hasData = false;
        }
      }
    }
  }

  // Calculates the depth of the data tree from given node
  public getDepth(node: GemSunburstData): number {
    // If node has subEntries
    if (node.subEntries != null && node.subEntries.length > 0) {
      // First get depth of first child node
      let cur = this.getDepth(node.subEntries[0]);
      // For every other child node
      for (let i = 1; i < node.subEntries.length; i++) {
        // Get depth of current child node
        let newCur = this.getDepth(node.subEntries[i]);
        // If depth of current child node is greater than the current maximum -> update maximum
        if (newCur > cur) cur = newCur;
      }
      // Depth of this node is equal to the depth of maximum plus one
      return cur + 1;
    }
    // If node has no subEntries depth is 0
    return 0;
  }

  // Converts the data of the GemSunburstData into data for the used doughnut chart
  private dataNodesToChart(data: GemSunburstData): void {
    // Initialized as empty
    this.labels = [];
    this.dataSets = [];

    let definedColors: string[];
    // If user did not define own colors use predefined colors
    if (this.userColors.length == 0) {
      definedColors = this.getColors();
    } else {
      definedColors = this.userColors;
    }
    this.legendColors = definedColors;

    // curNodes are all subEntries of this data
    let curNodes: (GemSunburstData & { color: string })[] = [];
    for (let i = 0; i < data.subEntries.length; ++i) {
      curNodes.push({
        ...data.subEntries[i],
        color: definedColors[i]
      })
    }
    // Initialize as empty
    let nextNodes: (GemSunburstData & { color: string })[] = [];
    let curDatas: { value: number, color: string }[] = [];
    let curLabels: string[] = [];
    let sunburstDatasTemp: { value: number, color: string }[][] = [];
    let sunburstLabelsTemp: string[][] = [];


    // There are as many doughnuts and data arrays as the depth of the tree is
    for (let j = 0; j < this.depth; j++) {
      // For every node in curNodes (for all current nodes)
      for (let i = 0; curNodes != null && i < curNodes.length; i++) {
        // Push value and label of current node to current data and current labels
        curDatas.push({
          value: curNodes[i].entry.value,
          color: curNodes[i].color
        });
        curLabels.push(curNodes[i].entry.label);
        // If current node has no subEntries
        if (curNodes[i].subEntries == null || curNodes[i].subEntries.length == 0) {
          // Push an empty node into the nextNodes (-> next cycle will deal with this subEntries as one empty node)
          nextNodes.push({ entry: { label: "", value: curNodes[i].entry.value, }, subEntries: [], color: '#00000000' });
        }
        // If current node has subEntries
        else {
          // Push every subNode of current node into the nextNodes (-> next cycle will deal with every subNode)
          for (let k = 0; k < curNodes[i].subEntries.length; k++) {
            nextNodes.push({
              ...curNodes[i].subEntries[k],
              color: curNodes[i].color
            });
          }
        }
      }
      // curNodes = nextNodes
      curNodes = [];
      curNodes = curNodes.concat(nextNodes);
      // nextNodes initialized new as empty
      nextNodes = [];
      // Push the current datas and labels into the temporary containers for all datas and labels
      sunburstDatasTemp.push(curDatas);
      sunburstLabelsTemp.push(curLabels);
      // Empty the current datas and current labels
      curDatas = [];
      curLabels = [];
    }

    // Data has to be reversed (doughnut goes counter-clockwise)
    sunburstDatasTemp.reverse();

    // Call methods for label refinement and color refinement
    this.changeDataNodesForLabels(sunburstDatasTemp, sunburstLabelsTemp);
  }

  // Adjusts the labels of the nodes to fit
  private changeDataNodesForLabels(
    sunburstDatas: { value: number, color: string }[][], sunburstLabels: string[][]
  ): void {
    // Initialize empty
    let sunburstDatasTemp: { value: number, color: string }[][] = [[]];
    let sunburstLabelsTemp: string[][] = [[]];

    // Insert 0-entrys: If tree on depth x has n nodes -> Insert n 0-entrys (empty nodes) into tree on depth x+1
    sunburstDatasTemp[0] = sunburstDatas[0];
    for (let j = 1; j < sunburstDatas[0].length; j++) {
      sunburstDatasTemp[j] = [{ value: 0, color: '#00000000' }];
      for (let i = 1; i < sunburstDatasTemp[j - 1].length; i++) {
        sunburstDatasTemp[j].push({ value: 0, color: '#00000000' });
      }
      sunburstDatasTemp[j] = sunburstDatasTemp[j].concat(sunburstDatas[j]);
    }
    // Concat all label arrays into one label array
    for (let i = sunburstLabels.length; i >= 0; i--) {
      sunburstLabelsTemp[0] = sunburstLabelsTemp[0].concat(sunburstLabels[i]);
    }
    // Pop first element (empty placeholder)
    sunburstLabelsTemp[0].shift();

    // Set datas and labels for this component
    this.dataSets = sunburstDatasTemp.map(d => {
      return {
        data: d.filter(e => !!e).map(e => e.value),
        backgroundColor: d.filter(e => !!e).map(e => e.color)
      };
    });
    this.labels = sunburstLabelsTemp[0];
    this.legendLabels = this.labels.slice(this.labels.length - this.subEntries.length);
  }

  public setLegendPosition(legendPosition: string) {
    this.legendPosition = legendPosition;
  }

  /** changes container style depending on legend position */
  public changeContainer() {
    switch (this.legendPosition) {
      case 'left':
        return 'wholeChartWithLegendLeftContainer';
      case 'top':
        return 'wholeChartWithLegendTopContainer';
      case 'bottom':
        return 'wholeChartWithLegendBottomContainer';
      case 'right':
        return 'wholeChartWithLegendRightContainer';
      default:
        return 'wholeChartWithLegendBottomContainer';
    }
  }

}
