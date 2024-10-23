import {GemVennDiagramData} from "@umlp/charts";

export function stringifyWithoutRealBuilder(data: GemVennDiagramData) {
  let dataString = JSON.stringify(data, (key, value) => {
    if (key == "realBuilder") {
      return undefined;
    } else {
      return value;
    }
  });
  return dataString;
}
