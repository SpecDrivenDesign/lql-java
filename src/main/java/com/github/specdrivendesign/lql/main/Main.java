package com.github.specdrivendesign.lql.main;

import com.github.specdrivendesign.lql.pkg.ast.Expression;
import com.github.specdrivendesign.lql.pkg.ast.expressions.Color;
import com.github.specdrivendesign.lql.pkg.bytecode.Bytecode;
import com.github.specdrivendesign.lql.pkg.env.Env;
import com.github.specdrivendesign.lql.pkg.errors.Errors;
import com.github.specdrivendesign.lql.pkg.lexer.Lexer;
import com.github.specdrivendesign.lql.pkg.parser.Parser;
import com.github.specdrivendesign.lql.pkg.signing.Signing;
import com.github.specdrivendesign.lql.pkg.testing.Testing;
import org.yaml.snakeyaml.Yaml;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

// Color constants
public class Main {
    public static final String colorReset   = "\033[0m";
    public static final String colorBlue    = "\033[34m";
    public static final String colorMagenta = "\033[35m";
    public static final String colorGreen   = "\033[32m";
    public static final String colorRed     = "\033[31m";
    public static final String colorYellow  = "\033[33m";

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Subcommand required: test, compile, exec, repl, validate, highlight, or export-contexts");
            System.out.println("Usage:");
            System.out.println("  lql test [--test-file=testcases.yml] [--fail-fast] [--verbose] [--output text|yaml]");
            System.out.println("  lql compile -expr \"<expression>\" -out <outfile> [-signed -private <private.pem>]");
            System.out.println("  lql exec -in <infile> [-signed -public <public.pem>]");
            System.out.println("  lql repl -expr \"<expression>\" [-format json|yaml]");
            System.out.println("  lql validate -expr \"<expression>\" | -in <file>");
            System.out.println("  lql highlight -expr \"<expression>\" [-theme mild|vivid|dracula|solarized]");
            System.out.println("  lql export-contexts -expr \"<expression>\" | -in <file>");
            System.exit(1);
        }

        String subcommand = args[0];
        try {
            switch (subcommand) {
                case "test":
                    runTestCmd(Arrays.copyOfRange(args, 1, args.length));
                    break;
                case "compile":
                    runCompileCmd(Arrays.copyOfRange(args, 1, args.length));
                    break;
                case "exec":
                    runExecCmd(Arrays.copyOfRange(args, 1, args.length));
                    break;
                case "repl":
                    runReplCmd(Arrays.copyOfRange(args, 1, args.length));
                    break;
                case "validate":
                    runValidateCmd(Arrays.copyOfRange(args, 1, args.length));
                    break;
                case "highlight":
                    runHighlightCmd(Arrays.copyOfRange(args, 1, args.length));
                    break;
                case "export-contexts":
                    runExportContextsCmd(Arrays.copyOfRange(args, 1, args.length));
                    break;
                default:
                    System.out.println("Unknown subcommand: " + subcommand);
                    System.exit(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

private static void runTestCmd(String[] args) throws Exception {
    // Simple flag parsing
    boolean failFast = false;
    boolean verbose = false;
    boolean benchmark = false;
    String outputFormat = "text";
    String testFile = "testcases.yml";
    for (int i = 0; i < args.length; i++) {
        switch (args[i]) {
            case "--fail-fast":
                failFast = true;
                break;
            case "--verbose":
                verbose = true;
                break;
            case "--benchmark":
                benchmark = true;
                break;
            case "--output":
                if (i + 1 < args.length) {
                    outputFormat = args[++i];
                }
                break;
            case "--test-file":
                if (i + 1 < args.length) {
                    testFile = args[++i];
                }
                break;
            case "--help":
                System.out.println("Usage for test command:");
                System.out.println("  --fail-fast    Stop on first failure");
                System.out.println("  --verbose      Verbose output");
                System.out.println("  --benchmark    Run each expression 1000 times and print benchmark info (only for function calls)");
                System.out.println("  --output       Output format: text or yaml (default: text)");
                System.out.println("  --test-file    YAML file containing test cases (default: testcases.yml)");
                System.exit(0);
                break;
        }
    }

    // Read the test file
    byte[] data = Files.readAllBytes(Paths.get(testFile));
    Yaml yaml = new Yaml();
    Object loaded = yaml.load(new String(data));
    List<Object> items = new ArrayList<>();
    if (loaded instanceof List) {
        items.addAll((List<Object>) loaded);
    } else if (loaded instanceof Map) {
        items.add(loaded);
    } else {
        System.out.println("Unexpected YAML format: " + loaded);
        System.exit(1);
    }

    List<Testing.TestCase> testCases = new ArrayList<>();
    for (Object o : items) {
        if (o instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) o;
            Testing.TestCase tc = new Testing.TestCase();
            tc.setDescription((String) map.get("description"));
            tc.setContext((Map<String, Object>) map.get("context"));
            tc.setExpression((String) map.get("expression"));
            tc.setExpectedError((String) map.get("expectedError"));
            tc.setExpectedErrorMessage((String) map.get("expectedErrorMessage"));
            tc.setExpectedResult(map.get("expectedResult"));
            Object skipObj = map.get("skip");
            if (skipObj instanceof Boolean) {
                tc.setSkip((Boolean) skipObj);
            }
            Object focusObj = map.get("focus");
            if (focusObj instanceof Boolean) {
                tc.setFocus((Boolean) focusObj);
            }
            testCases.add(tc);
        }
    }

    Env env = Env.newEnvironment();
    Testing.TestSuiteResult suiteResult = Testing.runTests(testCases, env, failFast, benchmark);

    if (outputFormat.equalsIgnoreCase("yaml")) {
        renderYAMLOutput(suiteResult);
    } else {
        renderTextOutput(suiteResult, verbose);
    }

    if (suiteResult.getFailed() > 0) {
        System.exit(1);
    }
    System.exit(0);
}




    private static void runCompileCmd(String[] args) throws Exception {
        // Parse flags similar to the Go version
        Map<String, String> flags = parseFlags(args);
        String expr = flags.get("expr");
        String inFile = flags.get("in");
        String outFile = flags.get("out");
        boolean signed = flags.containsKey("signed");
        String privateKeyFile = flags.getOrDefault("private", "private.pem");

        if ((expr == null || expr.isEmpty()) && (inFile == null || inFile.isEmpty())) {
            System.out.println("Either -expr or -in flag must be provided.");
            System.exit(1);
        }
        if (outFile == null || outFile.isEmpty()) {
            System.out.println("The -out flag is required.");
            System.exit(1);
        }
        if (inFile != null && !inFile.isEmpty()) {
            expr = new String(Files.readAllBytes(Paths.get(inFile))).trim();
        }

        Lexer lex = new Lexer(expr);
        byte[] byteCode;
        if (signed) {
            if (privateKeyFile == null || privateKeyFile.isEmpty()) {
                System.out.println("Private key file must be provided when -signed is true.");
                System.exit(1);
            }
            java.security.PrivateKey privateKey = Signing.loadPrivateKey(privateKeyFile);
            byteCode = lex.exportTokensSigned(privateKey);
        } else {
            byteCode = lex.exportTokens();
        }
        Files.write(Paths.get(outFile), byteCode);
        System.out.println("Compilation successful. Bytecode written to " + outFile);
    }

    private static void runExecCmd(String[] args) throws Exception {
        // Implementation analogous to the Go version.
        // Parse flags: -in, -expr, -signed, -public, -format
        Map<String, String> flags = parseFlags(args);
        String inFile = flags.get("in");
        String expr = flags.get("expr");
        boolean signed = flags.containsKey("signed");
        String publicKeyFile = flags.get("public");
        String contextFormat = flags.getOrDefault("format", "yaml");

        // Read context from stdin
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder contextData = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            contextData.append(line).append("\n");
        }
        Map<String, Object> ctx;
        if (contextData.toString().trim().length() > 0) {
            if (contextFormat.equalsIgnoreCase("json")) {
                ObjectMapper mapper = new ObjectMapper();
                ctx = mapper.readValue(contextData.toString(), Map.class);
            } else {
                Yaml yaml = new Yaml();
                ctx = yaml.load(contextData.toString());
            }
        } else {
            ctx = new HashMap<>();
        }

        if ((expr == null || expr.isEmpty()) && (inFile == null || inFile.isEmpty())) {
            System.out.println("Either -expr or -in flag must be provided.");
            System.exit(1);
        }
        if (expr != null && !expr.isEmpty()) {
            Lexer lex = new Lexer(expr);
            Parser p = new Parser(lex);
            Object ast = p.parseExpression();
            Env env = Env.newEnvironment();
            Object result = ((Expression) ast).eval(ctx, env);
            System.out.println("Execution result: " + result);
            return;
        }
        byte[] data = Files.readAllBytes(Paths.get(inFile));
        Object tokenStream;
        if (signed) {
            if (publicKeyFile == null || publicKeyFile.isEmpty()) {
                System.out.println("Public key file must be provided when -signed is true.");
                System.exit(1);
            }
            java.security.PublicKey pubKey = Signing.loadPublicKey(publicKeyFile);
            tokenStream = Bytecode.newByteCodeReaderFromSignedData(data, pubKey);
        } else {
            tokenStream = Bytecode.newByteCodeReader(data);
        }
        Parser p = new Parser((Lexer) tokenStream);
        Object ast = p.parseExpression();
        Env env = Env.newEnvironment();
        Object result = ((Expression) ast).eval(ctx, env);
        System.out.println("Execution result: " + result);
    }

    private static void runReplCmd(String[] args) throws Exception {
        Map<String, String> flags = parseFlags(args);
        String expr = flags.get("expr");
        if (expr == null || expr.isEmpty()) {
            System.out.println("The -expr flag is required in repl mode.");
            System.exit(1);
        }
        Lexer lex = new Lexer(expr);
        Parser p = new Parser(lex);
        Object ast = p.parseExpression();
        Env env = Env.newEnvironment();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String input;
            while (true) {
                System.out.print("Enter context (empty line to exit): ");
                input = br.readLine();
                if (input == null || input.trim().isEmpty()) {
                    System.out.println("Exiting REPL.");
                    break;
                }
                Map<String, Object> ctx;
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    ctx = mapper.readValue(input, Map.class);
                } catch (Exception e) {
                    System.out.println("Error parsing context: " + e.getMessage());
                    continue;
                }
                try {
                    Object result = ((Expression) ast).eval(ctx, env);
                    System.out.println(result);
                } catch (Exception e) {
                    System.out.println("Error executing expression: " + e.getMessage());
                }
            }
        }
    }

    private static void runValidateCmd(String[] args) throws Exception {
        Map<String, String> flags = parseFlags(args);
        String expr = flags.get("expr");
        String inFile = flags.get("in");
        String expression;
        if (inFile != null && !inFile.isEmpty()) {
            expression = new String(Files.readAllBytes(Paths.get(inFile))).trim();
        } else if (expr != null && !expr.isEmpty()) {
            expression = expr;
        } else {
            System.out.println("Either -expr or -in flag must be provided.");
            System.exit(1);
            return;
        }
        Lexer lex = new Lexer(expression);
        Parser p = new Parser(lex);
        try {
            p.parseExpression();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        System.exit(0);
    }

    private static void runHighlightCmd(String[] args) throws Exception {
        Map<String, String> flags = parseFlags(args);
        String expr = flags.get("expr");
        String theme = flags.getOrDefault("theme", "mild");
        if (expr == null || expr.isEmpty()) {
            System.out.println("The -expr flag is required.");
            System.exit(1);
        }
        // Enable color globally
        Color.setEnabled(true);

        Lexer lex = new Lexer(expr);
        Parser p = new Parser(lex);
        Object ast = p.parseExpression();

        // Apply theme based on flag
        switch (theme.toLowerCase()) {
            case "mild":
                Color.applyMildPalette();
                break;
            case "vivid":
                Color.applyVividPalette();
                break;
            case "dracula":
                Color.applyDraculaPalette();
                break;
            case "solarized":
                Color.applySolarizedPalette();
                break;
            default:
                System.out.println("Unknown theme '" + theme + "'. Using mild.");
                Color.applyMildPalette();
        }
        System.out.println(ast.toString());
    }

    private static void runExportContextsCmd(String[] args) throws Exception {
        Map<String, String> flags = parseFlags(args);
        String expr = flags.get("expr");
        String inFile = flags.get("in");
        String expression;
        if (inFile != null && !inFile.isEmpty()) {
            expression = new String(Files.readAllBytes(Paths.get(inFile)));
        } else if (expr != null && !expr.isEmpty()) {
            expression = expr;
        } else {
            System.out.println("Either -expr or -in flag must be provided.");
            System.exit(1);
            return;
        }
        Lexer lex = new Lexer(expression);
        List<String> identifiers = lex.extractContextIdentifiers();
        for (String id : identifiers) {
            System.out.println(id);
        }
    }

    // Simple flag parser: expects flags in form -flag value (or flag without value)
    private static Map<String, String> parseFlags(String[] args) {
        Map<String, String> flags = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                String key = args[i].replaceFirst("^-+", "");
                String value = "";
                if (i + 1 < args.length && !args[i+1].startsWith("-")) {
                    value = args[++i];
                }
                flags.put(key, value);
            }
        }
        return flags;
    }

    private static void renderTextOutput(Testing.TestSuiteResult suite, boolean verbose) {
        for (Testing.TestResult res : suite.getTestResults()) {
            if (!verbose && res.getStatus().equals("PASSED") && (res.getBenchmarkTime() == null || res.getBenchmarkTime().isEmpty())) {
                continue;
            }
            if (res.getStatus().equals("SKIPPED")) {
                continue;
            }
            System.out.println(colorBlue + "[Test #" + res.getTestId() + "] " + res.getDescription() + colorReset);
            System.out.println("    Expression : " + res.getExpression());
            System.out.println("    Context    : " + res.getContext());
            if ((res.getExpectedError() != null && !res.getExpectedError().isEmpty()) ||
                (res.getActualError() != null)) {
                if (res.getExpectedResult() != null) {
                    System.out.println("    Expected   : " + res.getExpectedResult());
                    System.out.println("    Actual     : " + res.getActualResult());
                }
                if (res.getExpectedError() != null && !res.getExpectedError().isEmpty()) {
                    System.out.println("    Expected Error Message: " + res.getExpectedError() + ": " + res.getExpectedErrorMessage());
                }
                System.out.println("    Actual Error Message  : " + res.getActualError());
            } else {
                System.out.println("    Expected   : " + res.getExpectedResult());
                System.out.println("    Actual     : " + res.getActualResult());
            }
            if (res.getBenchmarkTime() != null && !res.getBenchmarkTime().isEmpty()) {
                System.out.println("    Benchmark  : " + res.getBenchmarkTime() + " (" + res.getBenchmarkOpsSec() + " ops/sec)");
            }
            if (res.getActualError() != null && !res.getStatus().equals("PASSED")) {
                if (res.getErrLine() > 0 && res.getErrColumn() > 0) {
                    System.out.println(Errors.getErrorContext(res.getExpression(), res.getErrLine(), res.getErrColumn(), true));
                }
            }
            String statusColor = "";
            switch (res.getStatus()) {
                case "PASSED":
                    statusColor = colorGreen;
                    break;
                case "FAILED":
                    statusColor = colorRed;
                    break;
                case "SKIPPED":
                    statusColor = colorYellow;
                    break;
            }
            System.out.println("    Status     : " + statusColor + res.getStatus() + colorReset + "\n");
        }
        System.out.println("==============================================");
        System.out.println("Test Suite Completed");
        System.out.println("  " + colorGreen + "PASSED  " + colorReset + ": " + suite.getPassed());
        System.out.println("  " + colorYellow + "SKIPPED " + colorReset + ": " + suite.getSkipped());
        System.out.println("  " + colorRed + "FAILED  " + colorReset + ": " + suite.getFailed());
        System.out.println("  TOTAL   : " + suite.getTotal());
        System.out.println("==============================================");
    }

    private static void renderYAMLOutput(Testing.TestSuiteResult suite) {
        Yaml yaml = new Yaml();
        System.out.println(yaml.dump(suite));
    }
}
