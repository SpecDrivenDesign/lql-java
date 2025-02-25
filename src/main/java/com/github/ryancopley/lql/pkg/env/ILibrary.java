// --------- FILE START: "ILibrary.java" (converted from pkg/env/interface.go) ----------
package com.github.ryancopley.lql.pkg.env;

import com.github.ryancopley.lql.pkg.param.Param;
import java.util.List;

public interface ILibrary {
    Object call(String functionName, List<Param> args, int line, int col, int parenLine, int parenCol) throws Exception;
}
 // --------- FILE END: "ILibrary.java" ----------
