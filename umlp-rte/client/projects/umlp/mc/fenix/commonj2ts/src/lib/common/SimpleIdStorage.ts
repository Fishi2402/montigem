/* (c) https://github.com/MontiCore/monticore */
import {Log} from '../logging/Log';
import {LogUtil} from '../util/LogUtil';
import {UMLPArrayList} from '../common/UMLPArrayList';
import {IUMLPList} from '../common/IUMLPList';
import {IdStorage} from './IdStorage';

export class SimpleIdStorage implements IdStorage {
  idxToId: any;

  constructor() {
    this.idxToId = <any>({});
  }

  /**
   *
   * @param {number} idx
   * @return {Optional}
   */
  public getId(idx: number): number {
    if (/* containsKey */((m, k) => {
      if (m.entries == null) m.entries = [];
      for (let i = 0; i < m.entries.length; i++) if (m.entries[i].key == null && k == null || m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
        return true;
      }
      return false;
    })(<any>this.idxToId, idx)) {
      return (v => {
        if (v == null) throw new Error('value is null');
        return v;
      })(/* get */((m, k) => {
        if (m.entries == null) m.entries = [];
        for (let i = 0; i < m.entries.length; i++) if (m.entries[i].key == null && k == null || m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
          return m.entries[i].value;
        }
        return null;
      })(<any>this.idxToId, idx));
    } else {
      return null;
    }
  }

  /**
   *
   * @param {number} i
   * @param {IUMLPList} ids
   */
  public async addAll(i: number, ids: IUMLPList<number>) {
    Log.trace("Adding ids " + /* implicit toString */ (a => a ? '[' + a.join(', ') + ']' : 'null')(ids) + " at index " + i, "IdStorage " + LogUtil.getLoggerName());
    Log.trace("Initial state: " + this.idxToId, "IdStorage " + LogUtil.getLoggerName());
    const res: any = <any>({});
    const s: number = ids.size();
    {
      let array = /* keySet */((m) => {
        let r = [];
        if (m.entries == null) m.entries = [];
        for (let i = 0; i < m.entries.length; i++) r.push(m.entries[i].key);
        return r;
      })(<any>this.idxToId);
      for (let index = 0; index < array.length; index++) {
        let key = array[index];
        {
          const value: number = /* get */((m, k) => {
            if (m.entries == null) m.entries = [];
            for (let i = 0; i < m.entries.length; i++) if (m.entries[i].key == null && k == null || m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
              return m.entries[i].value;
            }
            return null;
          })(<any>this.idxToId, key);
          if (key >= i) {
            /* put */
            ((m, k, v) => {
              if (m.entries == null) m.entries = [];
              for (let i = 0; i < m.entries.length; i++) if (m.entries[i].key == null && k == null || m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                m.entries[i].value = v;
                return;
              }
              m.entries.push({
                key: k, value: v, getKey: function () {
                  return this.key
                }, getValue: function () {
                  return this.value
                }
              });
            })(<any>res, key + s, value);
          } else {
            /* put */
            ((m, k, v) => {
              if (m.entries == null) m.entries = [];
              for (let i = 0; i < m.entries.length; i++) if (m.entries[i].key == null && k == null || m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                m.entries[i].value = v;
                return;
              }
              m.entries.push({
                key: k, value: v, getKey: function () {
                  return this.key
                }, getValue: function () {
                  return this.value
                }
              });
            })(<any>res, key, value);
          }
        }
      }
    }
    for await(let id of ids) {
      {
        /* put */
        ((m, k, v) => {
          if (m.entries == null) m.entries = [];
          for (let i = 0; i < m.entries.length; i++) if (m.entries[i].key == null && k == null || m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
            m.entries[i].value = v;
            return;
          }
          m.entries.push({
            key: k, value: v, getKey: function () {
              return this.key
            }, getValue: function () {
              return this.value
            }
          });
        })(<any>res, i++, id);
      }
    }
    /* clear */
    (<any>this.idxToId).entries = [];
    this.idxToId = res;
    Log.trace("End state: " + this.idxToId, "IdStorage " + LogUtil.getLoggerName());
  }

  /**
   *
   * @param {*} changes
   */
  public set(changes: any) {
    Log.trace("Changes " + changes, "IdStorage " + LogUtil.getLoggerName());
    Log.trace("Initial state: " + this.idxToId, "IdStorage " + LogUtil.getLoggerName());
    this.idxToId.putAll(changes);
    Log.trace("Finished state: " + this.idxToId, "IdStorage " + LogUtil.getLoggerName());
  }

  /**
   *
   * @param {number[]} indicies
   */
  public async remove(indicies: Array<number> | IUMLPList<number>) {
    Log.trace("Removing indicies " + /* implicit toString */ (a => a ? '[' + a.join(', ') + ']' : 'null')(indicies), "IdStorage " + LogUtil.getLoggerName());
    Log.trace("Initial state: " + this.idxToId, "IdStorage " + LogUtil.getLoggerName());
    const offsets: any = <any>({});
    let i: number = 0;
    const sorted: IUMLPList<number> = <any>(new UMLPArrayList(indicies));
    sorted.sort(<any>(((funcInst: any) => {
      if (funcInst == null || typeof funcInst == 'function') {
        return funcInst
      }
      return (arg0, arg1) => (funcInst['compare'] ? funcInst['compare'] : funcInst).call(funcInst, arg0, arg1)
    })(null)));
    for await(let idx of sorted) {
      {
        i++;
        /* put */
        ((m, k, v) => {
          if (m.entries == null) m.entries = [];
          for (let i = 0; i < m.entries.length; i++) if (m.entries[i].key == null && k == null || m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
            m.entries[i].value = v;
            return;
          }
          m.entries.push({
            key: k, value: v, getKey: function () {
              return this.key
            }, getValue: function () {
              return this.value
            }
          });
        })(<any>offsets, idx, i);
      }
    }
    const res: any = <any>({});
    {
      let array = /* keySet */((m) => {
        let r = [];
        if (m.entries == null) m.entries = [];
        for (let i = 0; i < m.entries.length; i++) r.push(m.entries[i].key);
        return r;
      })(<any>this.idxToId);
      for (let index = 0; index < array.length; index++) {
        let key = array[index];
        {
          const value: number = /* get */((m, k) => {
            if (m.entries == null) m.entries = [];
            for (let i = 0; i < m.entries.length; i++) if (m.entries[i].key == null && k == null || m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
              return m.entries[i].value;
            }
            return null;
          })(<any>this.idxToId, key);
          if (/* contains */(indicies.indexOf(<any>(key)) >= 0)) {
            continue;
          }
          const floorKey: number = offsets.floorKey(key);
          if (floorKey != null) {
            const floorValue: number = /* get */((m, k) => {
              if (m.entries == null) m.entries = [];
              for (let i = 0; i < m.entries.length; i++) if (m.entries[i].key == null && k == null || m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                return m.entries[i].value;
              }
              return null;
            })(<any>offsets, floorKey);
            /* put */
            ((m, k, v) => {
              if (m.entries == null) m.entries = [];
              for (let i = 0; i < m.entries.length; i++) if (m.entries[i].key == null && k == null || m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                m.entries[i].value = v;
                return;
              }
              m.entries.push({
                key: k, value: v, getKey: function () {
                  return this.key
                }, getValue: function () {
                  return this.value
                }
              });
            })(<any>res, key - floorValue, value);
          } else {
            /* put */
            ((m, k, v) => {
              if (m.entries == null) m.entries = [];
              for (let i = 0; i < m.entries.length; i++) if (m.entries[i].key == null && k == null || m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                m.entries[i].value = v;
                return;
              }
              m.entries.push({
                key: k, value: v, getKey: function () {
                  return this.key
                }, getValue: function () {
                  return this.value
                }
              });
            })(<any>res, key, value);
          }
        }
      }
    }
    /* clear */
    (<any>this.idxToId).entries = [];
    this.idxToId = res;
    Log.trace("Finished state: " + this.idxToId, "IdStorage " + LogUtil.getLoggerName());
  }

  /**
   *
   * @param {number} idx
   * @return {boolean}
   */
  public containsIdx(idx: number): boolean {
    return /* containsKey */((m, k) => {
      if (m.entries == null) m.entries = [];
      for (let i = 0; i < m.entries.length; i++) if (m.entries[i].key == null && k == null || m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
        return true;
      }
      return false;
    })(<any>this.idxToId, idx);
  }

  /**
   *
   */
  public clear() {
    Log.trace("Clearing, was " + this.idxToId, "IdStorage " + LogUtil.getLoggerName());
    /* clear */
    (<any>this.idxToId).entries = [];
  }

  /**
   *
   * @return {number}
   */
  public size(): number {
    Log.trace("Returning size " + /* size */((m) => {
      if (m.entries == null) m.entries = [];
      return m.entries.length;
    })(<any>this.idxToId), "IdStorage " + LogUtil.getLoggerName());
    return /* size */((m) => {
      if (m.entries == null) m.entries = [];
      return m.entries.length;
    })(<any>this.idxToId);
  }

  /**
   *
   * @param {number} gemId
   * @return {boolean}
   */
  public removeId(gemId: number): boolean {
    Log.trace("Trying to remove gemId " + gemId, "IdStorage " + LogUtil.getLoggerName());
    let key: number = -1;
    {
      let array = /* keySet */((m) => {
        let r = [];
        if (m.entries == null) m.entries = [];
        for (let i = 0; i < m.entries.length; i++) r.push(m.entries[i].key);
        return r;
      })(<any>this.idxToId);
      for (let index = 0; index < array.length; index++) {
        let i = array[index];
        {
          const value: number = /* get */((m, k) => {
            if (m.entries == null) m.entries = [];
            for (let i = 0; i < m.entries.length; i++) if (m.entries[i].key == null && k == null || m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
              return m.entries[i].value;
            }
            return null;
          })(<any>this.idxToId, i);
          if (/* equals */((a, b) => {
            return a === b;
          })(value, gemId)) {
            key = i;
            break;
          }
        }
      }
    }
    if (key >= 0) {
      Log.trace("Removing gemId " + gemId + " at index " + key, "IdStorage " + LogUtil.getLoggerName());
      this.remove(/* singleton */[key]);
      return true;
    } else {
      Log.trace("Cant not find index of gemId " + gemId, "IdStorage " + LogUtil.getLoggerName());
      return false;
    }
  }
}

SimpleIdStorage["__class"] = "umlp.backendrte.common.SimpleIdStorage";
SimpleIdStorage["__interfaces"] = ["umlp.backendrte.common.IdStorage"];


