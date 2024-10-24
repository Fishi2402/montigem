import {GemSunburstData} from "../gen/gemsunburstcharttypes/GemSunburstData";
import {GemSDNshort} from "../gen/gemsunburstcharttypes/GemSDNshort";
import {GemSunburstDataBuilder} from "../gen/gemsunburstcharttypes/GemSunburstDataBuilder";
import {GemSunburstEntryBuilder} from "../gen/gemsunburstcharttypes/GemSunburstEntryBuilder";


// Checks if the given object is of type SDNshort (and not SunburstDataNode)
export function gemInstanceOfSDNshort(object: any): object is GemSDNshort {
  return 'c' in object;
}

// Converts a given SDNshort node to SunburstDataNode node
export function gemSdnShortToSDN(node: GemSDNshort): GemSunburstData {
  // Report error if SDNshort node has a wrong notation
  if (node.getCList() == null || node.getCList().length !== 2 || (typeof node.getC(0) !== typeof "String") || (typeof node.getC(1) !== typeof 0)) {
    console.error("Data can not be read, because of wrong format." + node.getCList());
    return new GemSunburstDataBuilder()
      .entry(
        new GemSunburstEntryBuilder().label('').value(0).build()
      ).build()
  }
  else {
    // Create SunburstDataNode node with content from SDNshort node and empty subNodes[]
    let sdnNode: GemSunburstData = new GemSunburstDataBuilder()
      .entry(
        new GemSunburstEntryBuilder().label(node.getC(0)).value(Number(node.getC(1))).build()
      )
      .subEntries([]).build()
    // If SDNshort node has subNodes
    if (node.getSNList() != null && node.getSNList().length > 0) {
      for (let i = 0; i < node.getSNList().length; i++) {
        // For every subNode call this method recursively and append as subNode to the SunburstDataNode node created before
        sdnNode.getSubEntriesList().push(gemSdnShortToSDN(node.getSNList()[i]));
      }
    }
    return sdnNode;
  }
}
