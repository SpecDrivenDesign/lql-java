// --------- FILE START: "Param.java" (converted from pkg/param/param.go) ----------
package com.github.specdrivendesign.lql.pkg.param;

public class Param {
    private Object value;
    private int line;
    private int column;

    public Param(Object value, int line, int column) {
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public Object getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
// --------- FILE END: "Param.java" ----------
