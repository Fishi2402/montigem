export class GemStatus {
  statusId: number;
  imageSource: string;
  label: string;
  threshold: number;

  constructor(statusId: number, threshold: number, imageSource: string, label?: string) {
    this.statusId = statusId;
    this.imageSource = imageSource;
    this.threshold = threshold;
    if (!label) {
      this.label = "";
    }
  }
}
