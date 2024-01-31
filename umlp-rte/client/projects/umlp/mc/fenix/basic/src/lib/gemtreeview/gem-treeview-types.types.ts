export class GemTreeviewEntry {
  expanded?: boolean;
  name?: string;
  children?: GemTreeviewEntry[];

  constructor(expanded?: boolean, name?: string, children?: GemTreeviewEntry[]) {
    this.expanded = expanded;
    this.name = name;
    this.children = children;
  }
}
