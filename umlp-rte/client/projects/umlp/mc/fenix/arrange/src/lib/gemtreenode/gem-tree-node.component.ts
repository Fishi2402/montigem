import {Component, EventEmitter, Input, OnInit, Output, TemplateRef, ViewChild, ViewContainerRef} from "@angular/core";

@Component({
  selector: "gem-tree-node",
  templateUrl: "./gem-tree-node.component.html",
})
export class GemTreeNode implements OnInit {
  @ViewChild('treeNodeTemplate', { static: true }) treeNodeTemplate: TemplateRef<any>;

  @Input() isExpanded = false;
  @Input() content: TemplateRef<any>;
  @Input() children: TemplateRef<any>[];

  @Output() onClick: EventEmitter<void> = new EventEmitter();
  @Output() onExpand: EventEmitter<void> = new EventEmitter();

  constructor(private viewContainerRef: ViewContainerRef) {}

  ngOnInit() {
    // Remove component's enclosing html tag
    this.viewContainerRef.createEmbeddedView(this.treeNodeTemplate);
    this.viewContainerRef.element.nativeElement.remove();
  }

  leftClick() {
    this.onClick.emit()
  }

  expand() {
    if (this.children) {
      this.isExpanded = !this.isExpanded;
      this.onExpand.emit();
    }
  }
}
