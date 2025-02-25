package com.github.specdrivendesign.lql.pkg.ast.expressions;

public class Color {
    private static boolean enabled = initColorEnabled();
    private static final String reset = "\033[0m";

    private static String punctuationColor;
    private static String stringColor;
    private static String numberColor;
    private static String operatorColor;
    private static String boolNullColor;
    private static String identifierColor;
    private static String libraryColor;
    private static String functionColor;
    private static String contextColor;

    public static final String PALETTE_MILD = "mild";
    public static final String PALETTE_VIVID = "vivid";
    public static final String PALETTE_DRACULA = "dracula";
    public static final String PALETTE_SOLARIZED = "solarized";

    static {
        String paletteName = System.getenv("COLOR_PALETTE");
        if (paletteName != null) {
            paletteName = paletteName.toLowerCase();
        } else {
            paletteName = "";
        }
        switch (paletteName) {
            case PALETTE_VIVID:
                applyVividPalette();
                break;
            case PALETTE_DRACULA:
                applyDraculaPalette();
                break;
            case PALETTE_SOLARIZED:
                applySolarizedPalette();
                break;
            case PALETTE_MILD:
                applyMildPalette();
                break;
            default:
                applySolarizedPalette();
                break;
        }
    }

    private static boolean initColorEnabled() {
        String val = System.getenv("ENABLE_COLORS");
        if (val == null) return false;
        val = val.toLowerCase();
        return val.equals("1") || val.equals("true");
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean en) {
        enabled = en;
    }

    public static String getReset() {
        return reset;
    }

    public static String getPunctuationColor() {
        return punctuationColor;
    }

    public static String getStringColor() {
        return stringColor;
    }

    public static String getNumberColor() {
        return numberColor;
    }

    public static String getOperatorColor() {
        return operatorColor;
    }

    public static String getBoolNullColor() {
        return boolNullColor;
    }

    public static String getIdentifierColor() {
        return identifierColor;
    }

    public static String getLibraryColor() {
        return libraryColor;
    }

    public static String getFunctionColor() {
        return functionColor;
    }

    public static String getContextColor() {
        return contextColor;
    }

    public static void applyMildPalette() {
        punctuationColor = "\033[38;2;92;99;112m";
        stringColor = "\033[38;2;152;195;121m";
        numberColor = "\033[38;2;209;154;102m";
        operatorColor = "\033[38;2;198;120;221m";
        boolNullColor = "\033[38;2;86;182;194m";
        identifierColor = "\033[38;2;229;192;123m";
        libraryColor = "\033[38;2;171;178;191m";
        functionColor = "\033[38;2;97;175;239m";
        contextColor = "\033[38;2;224;108;117m";
    }

    public static void applyVividPalette() {
        punctuationColor = "\033[38;2;255;128;0m";
        stringColor = "\033[38;2;255;85;85m";
        numberColor = "\033[38;2;0;255;0m";
        operatorColor = "\033[38;2;255;0;255m";
        boolNullColor = "\033[38;2;0;170;255m";
        identifierColor = "\033[38;2;255;215;0m";
        libraryColor = "\033[38;2;255;160;0m";
        functionColor = "\033[38;2;85;85;255m";
        contextColor = "\033[38;2;255;20;147m";
    }

    public static void applyDraculaPalette() {
        punctuationColor = "\033[38;2;98;114;164m";
        stringColor = "\033[38;2;241;250;140m";
        numberColor = "\033[38;2;189;147;249m";
        operatorColor = "\033[38;2;255;121;198m";
        boolNullColor = "\033[38;2;139;233;253m";
        identifierColor = "\033[38;2;80;250;123m";
        libraryColor = "\033[38;2;255;184;108m";
        functionColor = "\033[38;2;255;85;85m";
        contextColor = "\033[38;2;248;248;242m";
    }

    public static void applySolarizedPalette() {
        punctuationColor = "\033[38;2;88;110;117m";
        stringColor = "\033[38;2;42;161;152m";
        numberColor = "\033[38;2;133;153;0m";
        operatorColor = "\033[38;2;108;113;196m";
        boolNullColor = "\033[38;2;38;139;210m";
        identifierColor = "\033[38;2;181;137;0m";
        libraryColor = "\033[38;2;147;161;161m";
        functionColor = "\033[38;2;211;54;130m";
        contextColor = "\033[38;2;203;75;22m";
    }
}
