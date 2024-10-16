
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

export class GemBoxPlotEntryBuilder {
  private entry: GemBoxPlotEntry;
  constructor() {
    this.entry = new GemBoxPlotEntry(null, null);
  }

  public group(group: string): GemBoxPlotEntryBuilder {
    this.entry.group = group;
    return this;
  }

  public box(box: GemBoxPlotBox): GemBoxPlotEntryBuilder {
    this.entry.box = box;
    return this;
  }

  public points(points: number[]): GemBoxPlotEntryBuilder {
    this.entry.points = points;
    return this;
  }

  public color(color: string): GemBoxPlotEntryBuilder {
    this.entry.color = color;
    return this;
  }

  public build(): GemBoxPlotEntry {
    return this.entry;
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

export class GemBoxPlotBoxBuilder {
  private box: GemBoxPlotBox;
  constructor() {
    this.box = new GemBoxPlotBox(0, 0, 0, 0, 0);
  }

  public median(median: number): GemBoxPlotBoxBuilder {
    this.box.median = median;
    return this;
  }

  public lowerQuantile(lowerQuantile: number): GemBoxPlotBoxBuilder {
    this.box.lowerQuantile = lowerQuantile;
    return this;
  }

  public upperQuantile(upperQuantile: number): GemBoxPlotBoxBuilder {
    this.box.upperQuantile = upperQuantile;
    return this;
  }

  public lowerExtreme(lowerExtreme: number): GemBoxPlotBoxBuilder {
    this.box.median = lowerExtreme;
    return this;
  }

  public upperExtreme(upperExtreme: number): GemBoxPlotBoxBuilder {
    this.box.upperExtreme = upperExtreme;
    return this;
  }

  public build(): GemBoxPlotBox {
    return this.box;
  }
}
