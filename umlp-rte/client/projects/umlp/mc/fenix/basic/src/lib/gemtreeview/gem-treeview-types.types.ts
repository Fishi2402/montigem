export class GemTreeviewEntry {
  expanded?: boolean;
  name?: string;
  value?: string
  children?: GemTreeviewEntry[];

  constructor(expanded?: boolean, name?: string, value?:string, children?: GemTreeviewEntry[]) {
    this.expanded = expanded;
    this.name = name;
    this.children = children;
    this.value = value;
  }
}
