  export interface GemBarChartData {
       entries?: GemBarChartEntry[];
       labels?: string[];
  }
  export interface GemBarChartEntry {
       label?: string;
       stack?: string;
       data?: number[];
       backgroundColor?: string;
       hoverBackgroundColor?: string;
  }
