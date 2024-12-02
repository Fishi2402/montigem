/* (c) https://github.com/MontiCore/monticore */

export const previousIfUnchanged: <T, G extends WeakKey>(holder: G, data: T) => T
  = (<T, G extends WeakKey>() => {
    const map = new WeakMap<G, { value: T, hash: string }>();

    return (holder: G, data: T) => {
      const newHash = JSON.stringify(data);

      if (!map.has(holder) || newHash !== map.get(holder).hash) {
        map.set(holder, { value: data, hash: newHash });
      }

      return map.get(holder).value;
    }
  })();