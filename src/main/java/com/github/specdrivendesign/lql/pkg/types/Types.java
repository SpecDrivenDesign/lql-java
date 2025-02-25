package com.github.specdrivendesign.lql.pkg.types;

import com.github.specdrivendesign.lql.pkg.errors.Errors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Types {

    public static Double toFloat(Object val) {
        if (val instanceof Integer) {
            return ((Integer) val).doubleValue();
        } else if (val instanceof Long) {
            return ((Long) val).doubleValue();
        } else if (val instanceof Double) {
            return (Double) val;
        }
        return null;
    }

    public static Long toInt(Object val) {
        if (val instanceof Integer) {
            return ((Integer) val).longValue();
        } else if (val instanceof Long) {
            return (Long) val;
        } else if (val instanceof Double) {
            return ((Double) val).longValue();
        }
        return null;
    }

    public static boolean isInt(Object val) {
        return (val instanceof Integer) || (val instanceof Long);
    }

    public static boolean equals(Object left, Object right) {
        if (left == right) return true;
        if (left == null || right == null) return false;
        if (left instanceof Number && right instanceof Number) {
            Double lf = toFloat(left);
            Double rf = toFloat(right);
            return Math.abs(lf - rf) < 1e-9;
        }
        if (left instanceof List && right instanceof List) {
            List<?> listA = (List<?>) left;
            List<?> listB = (List<?>) right;
            if (listA.size() != listB.size()) return false;
            for (int i = 0; i < listA.size(); i++) {
                if (!equals(listA.get(i), listB.get(i))) return false;
            }
            return true;
        }
        if (left instanceof Map && right instanceof Map) {
            Map<?, ?> mapA = (Map<?, ?>) left;
            Map<?, ?> mapB = (Map<?, ?>) right;
            if (mapA.size() != mapB.size()) return false;
            for (Object key : mapA.keySet()) {
                if (!mapB.containsKey(key)) return false;
                if (!equals(mapA.get(key), mapB.get(key))) return false;
            }
            return true;
        }
        return left.equals(right);
    }


    public static boolean compare(Object left, Object right, String op, int line, int col) throws Exception {
        Double lf = toFloat(left);
        Double rf = toFloat(right);
        if (lf != null && rf != null) {
            switch (op) {
                case "<": return lf < rf;
                case ">": return lf > rf;
                case "<=": return lf <= rf;
                case ">=": return lf >= rf;
            }
        }
        if (left instanceof String && right instanceof String) {
            String ls = (String) left;
            String rs = (String) right;
            switch (op) {
                case "<": return ls.compareTo(rs) < 0;
                case ">": return ls.compareTo(rs) > 0;
                case "<=": return ls.compareTo(rs) <= 0;
                case ">=": return ls.compareTo(rs) >= 0;
            }
        }
        throw Errors.newSemanticError("'" + op + "' operator not allowed on given types", line, col);
    }

    public static Object parseNumber(String lit) {
        try {
            if (lit.contains(".") || lit.contains("e") || lit.contains("E")) {
                return Double.parseDouble(lit);
            } else {
                return Long.parseLong(lit);
            }
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public static List<Object> convertToInterfaceList(Object val) {
        if (val instanceof List<?>) {
            return (List<Object>) val;
        }
        return null;
    }

    public static Map<String, Object> convertToStringMap(Object val) {
        if (val instanceof Map<?, ?>) {
            Map<?, ?> m = (Map<?, ?>) val;
            Map<String, Object> result = new HashMap<>();
            for (Map.Entry<?, ?> entry : m.entrySet()) {
                result.put(String.valueOf(entry.getKey()), entry.getValue());
            }
            return result;
        }
        return null;
    }
}
