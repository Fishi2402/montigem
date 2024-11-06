import {GemVennDiagramData} from "@umlp/charts";

export function stringifyWithoutRealBuilder(data: any) {
  let dataString = JSON.stringify(data, (key, value) => {
    if (key == "realBuilder") {
      return undefined;
    } else {
      return value;
    }
  });
  return dataString;
}
