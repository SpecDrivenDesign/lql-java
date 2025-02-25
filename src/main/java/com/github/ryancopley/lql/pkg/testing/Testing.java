package com.github.ryancopley.lql.pkg.testing;

import com.github.ryancopley.lql.pkg.ast.Expression;
import com.github.ryancopley.lql.pkg.env.Env;
import com.github.ryancopley.lql.pkg.errors.Errors;
import com.github.ryancopley.lql.pkg.lexer.Lexer;
import com.github.ryancopley.lql.pkg.parser.Parser;
import com.github.ryancopley.lql.pkg.types.Types;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Testing {

    public static class TestCase {
        private String description;
        private Map<String, Object> context;
        private String expression;
        private String expectedError;
        private String expectedErrorMessage;
        private Object expectedResult;
        private boolean skip;
        private boolean focus;

        // Getters
        public String getDescription() { return description; }
        public Map<String, Object> getContext() { return context; }
        public String getExpression() { return expression; }
        public String getExpectedError() { return expectedError; }
        public String getExpectedErrorMessage() { return expectedErrorMessage; }
        public Object getExpectedResult() { return expectedResult; }
        public boolean isSkip() { return skip; }
        public boolean isFocus() { return focus; }

        // Setters
        public void setDescription(String description) { this.description = description; }
        public void setContext(Map<String, Object> context) { this.context = context; }
        public void setExpression(String expression) { this.expression = expression; }
        public void setExpectedError(String expectedError) { this.expectedError = expectedError; }
        public void setExpectedErrorMessage(String expectedErrorMessage) { this.expectedErrorMessage = expectedErrorMessage; }
        public void setExpectedResult(Object expectedResult) { this.expectedResult = expectedResult; }
        public void setSkip(boolean skip) { this.skip = skip; }
        public void setFocus(boolean focus) { this.focus = focus; }

        @Override
        public String toString() {
            return description;
        }
    }

    public static class TestResult {
        private int testId;
        private String description;
        private String expression;
        private Map<String, Object> context;
        private Object expectedResult;
        private String expectedError;
        private String expectedErrorMessage;
        private Object actualResult;
        private Exception actualError;
        private String status;
        private int errLine;
        private int errColumn;
        private String errorContext;
        private String benchmarkTime;
        private double benchmarkOpsSec;

        // Getters
        public int getTestId() { return testId; }
        public String getDescription() { return description; }
        public String getExpression() { return expression; }
        public Map<String, Object> getContext() { return context; }
        public Object getExpectedResult() { return expectedResult; }
        public String getExpectedError() { return expectedError; }
        public String getExpectedErrorMessage() { return expectedErrorMessage; }
        public Object getActualResult() { return actualResult; }
        public Exception getActualError() { return actualError; }
        public String getStatus() { return status; }
        public int getErrLine() { return errLine; }
        public int getErrColumn() { return errColumn; }
        public String getErrorContext() { return errorContext; }
        public String getBenchmarkTime() { return benchmarkTime; }
        public double getBenchmarkOpsSec() { return benchmarkOpsSec; }

        // Setters
        public void setTestId(int testId) { this.testId = testId; }
        public void setDescription(String description) { this.description = description; }
        public void setExpression(String expression) { this.expression = expression; }
        public void setContext(Map<String, Object> context) { this.context = context; }
        public void setExpectedResult(Object expectedResult) { this.expectedResult = expectedResult; }
        public void setExpectedError(String expectedError) { this.expectedError = expectedError; }
        public void setExpectedErrorMessage(String expectedErrorMessage) { this.expectedErrorMessage = expectedErrorMessage; }
        public void setActualResult(Object actualResult) { this.actualResult = actualResult; }
        public void setActualError(Exception actualError) { this.actualError = actualError; }
        public void setStatus(String status) { this.status = status; }
        public void setErrLine(int errLine) { this.errLine = errLine; }
        public void setErrColumn(int errColumn) { this.errColumn = errColumn; }
        public void setErrorContext(String errorContext) { this.errorContext = errorContext; }
        public void setBenchmarkTime(String benchmarkTime) { this.benchmarkTime = benchmarkTime; }
        public void setBenchmarkOpsSec(double benchmarkOpsSec) { this.benchmarkOpsSec = benchmarkOpsSec; }
    }

    public static class TestSuiteResult {
        private int passed;
        private int failed;
        private int skipped;
        private int total;
        private List<TestResult> testResults;

        public TestSuiteResult() {
            testResults = new ArrayList<>();
        }

        // Getters
        public int getPassed() { return passed; }
        public int getFailed() { return failed; }
        public int getSkipped() { return skipped; }
        public int getTotal() { return total; }
        public List<TestResult> getTestResults() { return testResults; }

        // Setters / Updaters
        public void setPassed(int passed) { this.passed = passed; }
        public void setFailed(int failed) { this.failed = failed; }
        public void setSkipped(int skipped) { this.skipped = skipped; }
        public void setTotal(int total) { this.total = total; }
        public void addTestResult(TestResult tr) { testResults.add(tr); }
    }

    // A new helper for deep equality checking.
    private static boolean deepEquals(Object a, Object b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        // If both are numbers, compare them as doubles with a tolerance.
        if (a instanceof Number && b instanceof Number) {
            double da = ((Number) a).doubleValue();
            double db = ((Number) b).doubleValue();
            return Math.abs(da - db) < 1e-9;
        }
        // If both are maps, compare keys and values recursively.
        if (a instanceof Map && b instanceof Map) {
            Map<?, ?> mapA = (Map<?, ?>) a;
            Map<?, ?> mapB = (Map<?, ?>) b;
            if (mapA.size() != mapB.size()) return false;
            for (Object key : mapA.keySet()) {
                if (!mapB.containsKey(key)) return false;
                if (!deepEquals(mapA.get(key), mapB.get(key))) return false;
            }
            return true;
        }
        // If both are lists, compare element by element.
        if (a instanceof List && b instanceof List) {
            List<?> listA = (List<?>) a;
            List<?> listB = (List<?>) b;
            if (listA.size() != listB.size()) return false;
            for (int i = 0; i < listA.size(); i++) {
                if (!deepEquals(listA.get(i), listB.get(i))) return false;
            }
            return true;
        }
        return a.equals(b);
    }


    public static TestSuiteResult runTests(List<TestCase> testCases, Env env, boolean failFast, boolean benchmark) {
        TestSuiteResult suiteResult = new TestSuiteResult();
        // Determine if any test is marked as focused.
        boolean focusMode = false;
        for (TestCase tc : testCases) {
            if (tc.isFocus()) {
                focusMode = true;
                break;
            }
        }
        int testId = 1;
        for (TestCase tc : testCases) {
            TestResult result = new TestResult();
            result.setTestId(testId++);
            result.setDescription(tc.getDescription());
            result.setExpression(tc.getExpression());
            result.setContext(tc.getContext());
            result.setExpectedResult(tc.getExpectedResult());
            result.setExpectedError(tc.getExpectedError());
            result.setExpectedErrorMessage(tc.getExpectedErrorMessage());

            if (focusMode && !tc.isFocus()) {
                result.setStatus("SKIPPED");
                suiteResult.setSkipped(suiteResult.getSkipped() + 1);
                suiteResult.addTestResult(result);
                continue;
            }
            if (tc.isSkip()) {
                result.setStatus("SKIPPED");
                suiteResult.setSkipped(suiteResult.getSkipped() + 1);
                suiteResult.addTestResult(result);
                continue;
            }
            suiteResult.setTotal(suiteResult.getTotal() + 1);
            try {
                // Create a lexer and parser to build the AST
                Lexer lex = new Lexer(tc.getExpression());
                Parser parser = new Parser(lex);
                Expression ast = parser.parseExpression();
                // Update the expression field to a canonical string representation
                result.setExpression(ast.toString());
                Object evalResult = ast.eval(tc.getContext(), env);
                result.setActualResult(evalResult);
                // Use deep equality for comparison
                if (deepEquals(evalResult, tc.getExpectedResult())) {
                    result.setStatus("PASSED");
                    suiteResult.setPassed(suiteResult.getPassed() + 1);
                } else {
                    result.setStatus("FAILED");
                    suiteResult.setFailed(suiteResult.getFailed() + 1);
                }
            } catch (Exception e) {
                result.setActualError(e);
                int[] pos = Errors.getErrorPosition(e);
                result.setErrLine(pos[0]);
                result.setErrColumn(pos[1]);
                result.setErrorContext(Errors.getErrorContext(tc.getExpression(), pos[0], pos[1], false));
                // If an error was expected and the error message contains expected text, mark as passed.
                if (tc.getExpectedError() != null && !tc.getExpectedError().isEmpty() &&
                    e.getMessage().contains(tc.getExpectedErrorMessage())) {
                    result.setStatus("PASSED");
                    suiteResult.setPassed(suiteResult.getPassed() + 1);
                } else {
                    result.setStatus("FAILED");
                    suiteResult.setFailed(suiteResult.getFailed() + 1);
                    if (failFast) {
                        suiteResult.addTestResult(result);
                        break;
                    }
                }
            }
            suiteResult.addTestResult(result);
        }
        return suiteResult;
    }
}
