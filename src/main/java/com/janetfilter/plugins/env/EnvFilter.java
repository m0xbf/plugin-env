package com.janetfilter.plugins.env;

import com.janetfilter.core.commons.DebugInfo;
import com.janetfilter.core.models.FilterRule;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvFilter {
    private static Map<String, String> myEnvironment;
    private static HashMap<String, String> theEnvironment;
    private static Map<String, String> theUnmodifiableEnvironment;

    public static void setRules(List<FilterRule> rules) {
        myEnvironment = new HashMap<>();

        for (FilterRule rule : rules) {
            String[] sections = rule.getRule().split("=", 2);
            if (2 != sections.length) {
                DebugInfo.output("Invalid record: " + rule + ", skipped.");
                continue;
            }

            myEnvironment.put(sections[0], sections[1]);
        }
    }

    public static String testGetEnv(String origin, String name) {
        String myValue = myEnvironment.get(name);

        if (null != myValue) {
            return myValue;
        }

        return origin;
    }

    public static Map<String, String> testGetEnv(Map<String, String> origin) {
        if (null == theUnmodifiableEnvironment) {
            Map<String, String> newEnv = new HashMap<>(origin);
            newEnv.putAll(myEnvironment);

            theUnmodifiableEnvironment = Collections.unmodifiableMap(newEnv);
        }

        return theUnmodifiableEnvironment;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> testEnvironment(Map<String, String> origin) {
        if (null == theEnvironment) {
            theEnvironment = new HashMap<>(origin);
            theEnvironment.putAll(myEnvironment);
        }

        return (Map<String, String>) theEnvironment.clone();
    }
}
