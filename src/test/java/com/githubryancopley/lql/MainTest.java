package com.githubryancopley.lql;

import com.github.ryancopley.lql.pkg.env.Env;
import com.github.ryancopley.lql.pkg.testing.Testing;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.yaml.snakeyaml.Yaml;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    @ParameterizedTest
    @MethodSource("testCasesProvider")
    public void runTestCase(Testing.TestCase tc) {

        Env env = Env.newEnvironment();
        Testing.TestSuiteResult suiteResult = Testing.runTests(List.of(tc), env, false, false);
        // just a sanity check
        assertEquals(1, suiteResult.getTestResults().size());
        final var result = suiteResult.getTestResults().getFirst();
        assertNotEquals("FAILED", result.getStatus(), () -> generateFailureMessage(result));
    }

    private String generateFailureMessage(Testing.TestResult result) {
        Yaml yaml = new Yaml();
        return yaml.dump(result);
    }

    private static List<Testing.TestCase> testCasesProvider() throws Exception {
        final var dataStream = MainTest.class.getResourceAsStream("/testcases.yml");
        byte[] data = dataStream.readAllBytes();
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
        return testCases;
    }
}


