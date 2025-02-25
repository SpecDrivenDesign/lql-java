package com.github.ryancopley.lql.pkg.env;

import com.github.ryancopley.lql.pkg.env.libraries.Array;
import com.github.ryancopley.lql.pkg.env.libraries.Cond;
import com.github.ryancopley.lql.pkg.env.libraries.Math;
import com.github.ryancopley.lql.pkg.env.libraries.Regex;
import com.github.ryancopley.lql.pkg.env.libraries.StringLib;
import com.github.ryancopley.lql.pkg.env.libraries.TimeLib;
import com.github.ryancopley.lql.pkg.env.libraries.TypeLib;

import java.util.HashMap;
import java.util.Map;

public class Env {
    private Map<String, ILibrary> libraries;

    public Env() {
        libraries = new HashMap<>();
        libraries.put("time", new TimeLib());
        libraries.put("math", new Math());
        libraries.put("string", new StringLib());
        libraries.put("regex", new Regex());
        libraries.put("array", new Array());
        libraries.put("cond", new Cond());
        libraries.put("type", new TypeLib());
    }

    public static Env newEnvironment() {
        return new Env();
    }

    public ILibrary getLibrary(String name) {
        return libraries.get(name);
    }
}
