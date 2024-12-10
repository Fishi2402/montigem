/**
 * @deprecated will be replaced with DeSers
 */
export function StringifyForBuilder(potentialBuilder: any) : string {
  return JSON.stringify(potentialBuilder, (key, value) => {
    if (key == "realBuilder") {
      return undefined;
    }
    else {
      return value;
    }
  });
}
