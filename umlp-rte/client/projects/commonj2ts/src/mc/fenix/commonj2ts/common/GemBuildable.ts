export interface GemBuildable {
  build(): Promise<any>;

  isBuildFinished(): boolean;
}
