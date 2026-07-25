import {Collection} from "../javautil";

export class StringHelper {
  public static valueOf(obj: any) {
    // TODO better matching of Java's obj.toString()?
    return obj === null ? "null" : `${obj}`;
  }

  public static format(str: string, ...val: string[]): string {
    let res = str;
    for (let v of val) {
      res = str.replace("%s", v);
    }
    return res;
  }

  // TODO do we want to offer a non async join? This would not work on IUMLPList
  public static async join(delimiter: string, strs: string[] | Collection<string>): Promise<string> {
    if(Array.isArray(strs)) {
      return strs.join(delimiter);
    } else {
      let res = "";
      const size : number = await strs.size();
      let it = 0;
      for await(const elem of strs) {
        res+=elem;
        it++;
        if(it < size) {
          res+=delimiter;
        }
      }
      return res;
    }
  }

  public static split(input: string, regex: string, limit?: number): string[] {
    const re = this.compileJavaRegexPattern(regex);
    return this.splitImpl(re, input, limit ?? 0, false);
  }

  private static compileJavaRegexPattern(regex: string): RegExp {
    // TODO does not support all Java RegExp features:
    // * COMMENTS, UNICODE_CASE, UNICODE_CHARACTER_CLASS and UNIX_LINES embedded flags
    // * embedded flag expressions in the middle, e.g. "a(?i)b"
    // ...

    // matches "(?i)(?ud...)..." prefix
    const embeddedFlagsExp = /^(?:\(\?[ixmsud]+\))*/;
    const m = embeddedFlagsExp.exec(regex);

    let flags: string | undefined = undefined;
    if(m != null && m.length > 0) {
      const embeddedFlags = m[0];
      flags = this.parseEmbeddedFlags(embeddedFlags);
      regex = regex.substring(embeddedFlags.length);
    }

    return new RegExp(regex, flags);
  }

  private static parseEmbeddedFlags(embeddedFlags: string): string {
    let flags = "";
    for (let char of embeddedFlags) {
      switch (char) {
        case "g":
        case "i":
        case "m":
        case "s":
          flags += char;
          break;
        case "x":
        case "u":
        case "U":
        case "d":
          console.warn(`Ignoring unsupported embedded flag '${char}' in regular expression`);
          break;
      }
    }
    return flags;
  }

  private static splitImpl(re: RegExp, input: string, limit: number, withDelimiters: boolean): string[] {
    // Port of java/util/regex/Pattern.java
    // This is necessary because String.prototype.split(..., limit) behaves differently:
    // * It does not output the entire remaining substring after the last separator match.
    // * The array also contains the captured groups for each separator match

    // add g flag so lastIndex gets updated
    if (!re.global) {
      re = new RegExp(re, re.flags + "g");
    }

    let matchCount = 0;
    let index = 0;
    const matchLimited = limit > 0;
    const matchList: string[] = [];
    let m: RegExpExecArray | null = null;

    // Add segments before each match found
    while ((m = this.findNext(re, input, m)) !== null) {
      const mStart = m.index;
      const mEnd = re.lastIndex;

      if(!matchLimited || matchCount < limit - 1) {
        if(index === 0 && index === mStart && mStart === mEnd) {
          // no empty leading substring included for zero-width match
          // at the beginning of the input char sequence.
          continue;
        }
        const match = input.substring(index, mStart);
        matchList.push(match);
        index = mEnd;
        if (withDelimiters) {
          matchList.push(input.substring(mStart, index).toString());
        }
        ++matchCount;
      } else if (matchCount === limit - 1) { // last one
        const match = input.substring(index, input.length);
        matchList.push(match);
        index = mEnd;
        ++matchCount;
      }
    }

    // If no match was found, return this
    if (index === 0) {
      return [input];
    }

    // Add remaining segment
    if (!matchLimited || matchCount < limit) {
      matchList.push(input.substring(index, input.length));
    }

    // Construct result
    let resultSize = matchList.length;
    if (limit === 0) {
      while (resultSize > 0 && matchList[resultSize-1].length === 0) {
        resultSize--;
      }
    }

    return matchList.slice(0, resultSize);
  }

  private static findNext(re: RegExp, input: string, lastM: RegExpExecArray | null): RegExpExecArray {
    const lastMStart = lastM != null ? lastM.index : -1;
    if (re.lastIndex === lastMStart) {
      re.lastIndex++; // if the last match was zero-length, search from next index
    }

    return re.exec(input);
  }
}
