import { AbstractGraph }  from "graphology-types";

// Based on: Ortali & Tollis, "A New Framework for Hierarchical Drawings", 2019.
// Edges draw directly from source to target, no dummy nodes, overlapping accepted.

export type ChannelLayoutOptionsInterface = {
  levelSeparation?: number
  channelWidth?: number
  useDummyNodes?: boolean
}

type ReversedEdgeInfo = { originalKey: string, reversedKey: string, wasDirected: boolean };
type SelfLoopInfo = { key: string, source: string, attributes: Record<string, any>, wasDirected: boolean };

export class ChannelLayout  {
  private readonly levelSeparation : number;
  private readonly channelWidth : number;
  private readonly useDummyNodes : boolean;

  private constructor(options?: ChannelLayoutOptionsInterface) {
    this.levelSeparation = options?.levelSeparation ?? 250;
    this.channelWidth = options?.channelWidth ?? 400;
    this.useDummyNodes = options?.useDummyNodes !== false;
  }

  static assign(g: AbstractGraph, options?: ChannelLayoutOptionsInterface): void {
    new ChannelLayout(options).run(g);
  }

  private run(g: AbstractGraph): void {
    if (g.order === 0) return;
    const components = this.findWeakComponents(g);
    let xOffset = 0;
    for (const component of components) {
      xOffset += this.layoutComponent(g, component, xOffset) + this.channelWidth;
    }
  }

  private layoutComponent(g: AbstractGraph, nodes: string[], xOffset: number): number {
    if (nodes.length === 1) {
      g.setNodeAttribute(nodes[0], "x", xOffset);
      g.setNodeAttribute(nodes[0], "y", 0);
      return this.channelWidth;
    }

    const { reversedEdges, selfLoops } = this.removeCycles(g, nodes);
    const ranks = this.assignRanks(g, nodes);
    const channels = this.decomposeChannels(g, nodes, ranks);
    const ordered = this.orderChannels(g, channels);

    const nodeToChannelIdx = new Map<string, number>();
    ordered.forEach((channel, ci) => {
      const x = xOffset + ci * this.channelWidth;
      channel.forEach((node) => {
        nodeToChannelIdx.set(node, ci);
        g.setNodeAttribute(node, "x", x);
        g.setNodeAttribute(node, "y", -(ranks.get(node) ?? 0) * this.levelSeparation);
      });
    });

    this.restoreEdges(g, reversedEdges, selfLoops);
    if (this.useDummyNodes) {
      this.routeChannelSkipEdges(g, nodes, ranks, nodeToChannelIdx);
    }

    return ordered.length * this.channelWidth;
  }

  /*
  * Step 1:
  * Remove self-loops and reverse back edges so rank assignment can run on a DAG
  */
  private removeCycles(
    g: AbstractGraph,
    nodes: string[]
  ): { reversedEdges: ReversedEdgeInfo[]; selfLoops: SelfLoopInfo[] } {
    const nodeSet = new Set(nodes);
    const selfLoops: SelfLoopInfo[] = [];
    const selfLoopKeys: string[] = [];

    g.forEachEdge(edgeKey => {
      if (!nodeSet.has(g.source(edgeKey))) return;
      if (g.source(edgeKey) === g.target(edgeKey)) {
        selfLoops.push({
          key: edgeKey,
          source: g.source(edgeKey),
          attributes: { ...g.getEdgeAttributes(edgeKey) },
          wasDirected: g.isDirected(edgeKey),
        });
        selfLoopKeys.push(edgeKey);
      }
    });
    for (const key of selfLoopKeys) g.dropEdge(key);
    const inDeg = new Map<string, number>();
    const outDeg = new Map<string, number>();
    const remaining = new Set(nodes);

    for (const n of nodes) { inDeg.set(n, 0); outDeg.set(n, 0); }
    g.forEachEdge(edgeKey => {
      const s = g.source(edgeKey), t = g.target(edgeKey);
      if (!nodeSet.has(s) || !nodeSet.has(t)) return;
      outDeg.set(s, (outDeg.get(s) ?? 0) + 1);
      inDeg.set(t, (inDeg.get(t) ?? 0) + 1);
    });

    const removeNode = (n: string) => {
      remaining.delete(n);
      for (const e of g.outboundEdges(n)) {
        const t = g.target(e);
        if (remaining.has(t)) inDeg.set(t, (inDeg.get(t) ?? 1) - 1);
      }
      for (const e of g.inboundEdges(n)) {
        const s = g.source(e);
        if (remaining.has(s)) outDeg.set(s, (outDeg.get(s) ?? 1) - 1);
      }
    };

    const leftOrder: string[] = [];
    const rightOrder: string[] = [];

    while (remaining.size > 0) {
      let changed = true;
      while (changed) {
        changed = false;
        for (const n of Array.from(remaining)) {
          if (outDeg.get(n) === 0) { rightOrder.push(n); removeNode(n); changed = true; break; }
        }
        for (const n of Array.from(remaining)) {
          if (inDeg.get(n) === 0) { leftOrder.push(n); removeNode(n); changed = true; break; }
        }
      }
      if (remaining.size === 0) break;
      let bestNode = "";
      let bestScore = Number.NEGATIVE_INFINITY;
      for (const n of remaining) {
        const score = (outDeg.get(n) ?? 0) - (inDeg.get(n) ?? 0);
        if (score > bestScore) { bestScore = score; bestNode = n; }
      }
      leftOrder.push(bestNode);
      removeNode(bestNode);
    }

    const nodeOrder = leftOrder.concat(rightOrder.reverse());
    const positions = new Map<string, number>();
    nodeOrder.forEach((n, i) => positions.set(n, i));

    const reversedEdges: ReversedEdgeInfo[] = [];
    for (const edgeKey of Array.from(g.edges())) {
      const s = g.source(edgeKey), t = g.target(edgeKey);
      if (!nodeSet.has(s) || !nodeSet.has(t)) continue;
      if ((positions.get(s) ?? 0) <= (positions.get(t) ?? 0)) continue;

      const attrs = { ...g.getEdgeAttributes(edgeKey) };
      const wasDirected = g.isDirected(edgeKey);
      g.dropEdge(edgeKey);
      const revKey = `rev_${edgeKey}`;
      wasDirected
        ? g.addDirectedEdgeWithKey(revKey, t, s, attrs)
        : g.addUndirectedEdgeWithKey(revKey, t, s, attrs);
      reversedEdges.push({ originalKey: edgeKey, reversedKey: revKey, wasDirected });
    }

    return { reversedEdges, selfLoops };
  }

  /*
  * Step 2:
  * Assign each node a rank equal to the length of the longest path from any source (longest-path layering)
  */
  private assignRanks(g: AbstractGraph, nodes: string[]): Map<string, number> {
    const nodeSet = new Set(nodes);
    const ranks = new Map<string, number>();
    const inDeg = new Map<string, number>();

    for (const n of nodes) inDeg.set(n, 0);
    g.forEachEdge(e => {
      const s = g.source(e), t = g.target(e);
      if (!nodeSet.has(s) || !nodeSet.has(t)) return;
      inDeg.set(t, (inDeg.get(t) ?? 0) + 1);
    });

    const queue: string[] = nodes.filter(n => inDeg.get(n) === 0);
    for (const n of queue) ranks.set(n, 0);

    let head = 0;
    while (head < queue.length) {
      const node = queue[head++];
      const rank = ranks.get(node) ?? 0;
      for (const e of g.outboundEdges(node)) {
        const t = g.target(e);
        if (!nodeSet.has(t)) continue;
        const next = rank + 1;
        if ((ranks.get(t) ?? -1) < next) ranks.set(t, next);
        inDeg.set(t, (inDeg.get(t) ?? 1) - 1);
        if (inDeg.get(t) === 0) queue.push(t);
      }
    }

    for (const n of nodes) if (!ranks.has(n)) ranks.set(n, 0);
    return ranks;
  }

  /*
  * Step 3:
  * Partition nodes into vertex-disjoint chains (channels) using a greedy path cover.
  * Each node extends an existing channel whose tail is a direct predecessor; otherwise a new channel starts.
  */
  private decomposeChannels(g: AbstractGraph, nodes: string[], ranks: Map<string, number>): string[][] {
    const nodeSet = new Set(nodes);
    const sorted = [...nodes].sort((a, b) => (ranks.get(a) ?? 0) - (ranks.get(b) ?? 0));

    const nodeToChannel = new Map<string, number>();
    const channelTail = new Map<number, string>();
    const channels: string[][] = [];

    for (const node of sorted) {
      let bestChannel = -1;
      let bestRank = -1;

      for (const edgeKey of g.inboundEdges(node)) {
        const pred = g.source(edgeKey);
        if (!nodeSet.has(pred)) continue;
        const predChannelIdx = nodeToChannel.get(pred);
        if (predChannelIdx === undefined) continue;
        if (channelTail.get(predChannelIdx) !== pred) continue;
        const predRank = ranks.get(pred) ?? 0;
        if (predRank > bestRank) { bestRank = predRank; bestChannel = predChannelIdx; }
      }

      if (bestChannel >= 0) {
        channels[bestChannel].push(node);
        nodeToChannel.set(node, bestChannel);
        channelTail.set(bestChannel, node);
      } else {
        const idx = channels.length;
        channels.push([node]);
        nodeToChannel.set(node, idx);
        channelTail.set(idx, node);
      }
    }

    return channels;
  }

  /*
  * Step 4:
  * Reorder channels to reduce edge crossings using an iterative barycenter heuristic
  */
  private orderChannels(g: AbstractGraph, channels: string[][]): string[][] {
    if (channels.length <= 1) return channels;

    const nodeToChannelIdx = new Map<string, number>();
    channels.forEach((ch, ci) => ch.forEach(n => nodeToChannelIdx.set(n, ci)));

    let order = channels.map((_, i) => i);

    for (let iter = 0; iter < 4; iter++) {
      const posOf = new Map<number, number>();
      order.forEach((ci, pos) => posOf.set(ci, pos));

      const barycenters = order.map(ci => {
        const neighborPositions: number[] = [];
        for (const node of channels[ci]) {
          for (const e of [...g.inboundEdges(node), ...g.outboundEdges(node)]) {
            const s = g.source(e), t = g.target(e);
            const neighbor = s === node ? t : s;
            const nci = nodeToChannelIdx.get(neighbor);
            if (nci !== undefined && nci !== ci) neighborPositions.push(posOf.get(nci) ?? 0);
          }
        }
        if (neighborPositions.length === 0) return posOf.get(ci) ?? 0;
        return neighborPositions.reduce((a, b) => a + b, 0) / neighborPositions.length;
      });

      order = order
        .map((ci, i) => ({ ci, bc: barycenters[i] }))
        .sort((a, b) => a.bc - b.bc || a.ci - b.ci)
        .map(x => x.ci);
    }

    return order.map(ci => channels[ci]);
  }

  /*
  * Per Ortali & Tollis: cross-channel skip edges get one bend, adjacent/same-channel edges draw straight.
  */
  private routeChannelSkipEdges(
    g: AbstractGraph,
    nodes: string[],
    ranks: Map<string, number>,
    nodeToChannelIdx: Map<string, number>
  ): void {
    const nodeSet = new Set(nodes);

    g.forEachEdge((edgeKey, _attrs, source, target) => {
      if (!nodeSet.has(source) || !nodeSet.has(target)) return;

      const srcCh = nodeToChannelIdx.get(source);
      const tgtCh = nodeToChannelIdx.get(target);
      if (srcCh === undefined || tgtCh === undefined) return;
      if (Math.abs(srcCh - tgtCh) <= 1) return; // same or adjacent channel → straight line

      const srcRank = ranks.get(source) ?? 0;
      const tgtRank = ranks.get(target) ?? 0;
      const srcX = g.getNodeAttribute(source, "x") as number;
      const tgtX = g.getNodeAttribute(target, "x") as number;

      // Direction from source to target channel
      const direction = Math.sign(tgtX - srcX);
      // Bend x: one corridor-width into the gap beside the source, towards the target
      const bendX = srcX + direction * (this.channelWidth / 2);
      // Bend y: one rank above the target (towards source), per Y(be) = Y(v) - 1 in paper
      const step = srcRank < tgtRank ? 1 : -1;
      const bendY = -(tgtRank - step) * this.levelSeparation;

      g.setEdgeAttribute(edgeKey, "waypoints", [{ x: bendX, y: bendY }]);
      g.setEdgeAttribute(edgeKey, "type", "polyline");
    });
  }

  // Restores the original graph semantics after layout
  private restoreEdges(g: AbstractGraph, reversedEdges: ReversedEdgeInfo[], selfLoops: SelfLoopInfo[]): void {
    for (const { originalKey, reversedKey, wasDirected } of reversedEdges) {
      if (!g.hasEdge(reversedKey)) continue;
      const attrs = { ...g.getEdgeAttributes(reversedKey) };
      const s = g.source(reversedKey);
      const t = g.target(reversedKey);
      g.dropEdge(reversedKey);
      wasDirected
        ? g.addDirectedEdgeWithKey(originalKey, t, s, attrs)
        : g.addUndirectedEdgeWithKey(originalKey, t, s, attrs);
    }
    for (const { key, source, attributes, wasDirected } of selfLoops) {
      wasDirected
        ? g.addDirectedEdgeWithKey(key, source, source, attributes)
        : g.addUndirectedEdgeWithKey(key, source, source, attributes);
    }
  }

  // Finds weakly connected components using iterative DFS
  private findWeakComponents(g: AbstractGraph): string[][] {
    const visited = new Set<string>();
    const components: string[][] = [];

    for (const start of g.nodes()) {
      if (visited.has(start)) continue;
      const component: string[] = [];
      const stack = [start];
      while (stack.length > 0) {
        const node = stack.pop()!;
        if (visited.has(node)) continue;
        visited.add(node);
        component.push(node);
        for (const neighbor of g.neighbors(node)) {
          if (!visited.has(neighbor)) stack.push(neighbor);
        }
      }
      components.push(component);
    }

    return components;
  }
}
