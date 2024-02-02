import {
  GemSunburstData as GemSunburstDataI,
  GemSDNshort as GemSDNshortI,
  GemSunburstEntry as GemSunburstEntryI
} from './gem-sunburst-chart-types/gem-sunburst-chart-types.types-gen';


// Checks if the given object is of type SDNshort (and not SunburstDataNode)
export function gemInstanceOfSDNshort(object: any): object is GemSDNshort {
  return 'c' in object;
}

// Converts a given SDNshort node to SunburstDataNode node
export function gemSdnShortToSDN(node: GemSDNshort): GemSunburstData {
  // Report error if SDNshort node has a wrong notation
  if (node.c == null || node.c.length !== 2 || (typeof node.c[0] !== typeof "String") || (typeof node.c[1] !== typeof 0)) {
    console.error("Data can not be read, because of wrong format." + node.c);
    return { entry: { label: '', value: 0, } };
  }
  else {
    // Create SunburstDataNode node with content from SDNshort node and empty subNodes[]
    let sdnNode: GemSunburstData = { entry: { label: node.c[0], value: node.c[1], }, subEntries: [] };
    // If SDNshort node has subNodes
    if (node.sN != null && node.sN.length > 0) {
      for (let i = 0; i < node.sN.length; i++) {
        // For every subNode call this method recursively and append as subNode to the SunburstDataNode node created before
        sdnNode.subEntries.push(gemSdnShortToSDN(node.sN[i]));
      }
    }
    return sdnNode;
  }
}

export class GemSunburstData implements GemSunburstDataI {
  entry?: GemSunburstEntry;
  subEntries?: GemSunburstData[];
  constructor(entry?: GemSunburstEntry, subEntries?: GemSunburstData[]) {
    this.entry = entry;
    this.subEntries = subEntries;
  }
}
export class GemSunburstEntry implements GemSunburstEntryI {
  label?: string;
  value?: number;
  constructor(label?: string, value?: number) {
    this.label = label;
    this.value = value;
  }
}
export class GemSDNshort implements GemSDNshortI {
  c?: any[];
  sN?: GemSDNshort[];
  constructor(c?: any[], sN?: GemSDNshort[]) {
    this.c = c;
    this.sN = sN;
  }
}
