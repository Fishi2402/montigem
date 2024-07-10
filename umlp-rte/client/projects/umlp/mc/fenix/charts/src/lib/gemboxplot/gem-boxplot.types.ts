
export class GemBoxPlotEntry {
  group: string;
  box: GemBoxPlotBox;
  points?: number[];
  color?: string;
  constructor(group: string, box: GemBoxPlotBox,points?: number[], color?: string) {
    this.group=group;
    this.box = box;

    if(points) {
      this.points = points;
    }
    if(color) {
      this.color = color;
    }
  }
}

export class GemBoxPlotBox {
  median: number;
  lowerQuantile: number;
  upperQuantile: number;
  lowerExtreme: number;
  upperExtreme: number;

  constructor(median: number, lowerquantile: number,
                upperquantile: number, lowerExtreme: number, upperExtreme: number) {
    this.median = median;
    this.lowerQuantile = lowerquantile;
    this.upperQuantile = upperquantile;
    this.lowerExtreme = lowerExtreme;
    this.upperExtreme = upperExtreme;
  }
}
