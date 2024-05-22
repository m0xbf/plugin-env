package com.janetfilter.plugins.env;

import com.janetfilter.core.Environment;
import com.janetfilter.core.plugin.MyTransformer;
import com.janetfilter.core.plugin.PluginConfig;
import com.janetfilter.core.plugin.PluginEntry;

import java.util.ArrayList;
import java.util.List;

public class EnvFilterPlugin implements PluginEntry {
    private static final String PLUGIN_NAME = "ENV";
    private final List<MyTransformer> transformers = new ArrayList<>();

    @Override
    public void init(Environment environment, PluginConfig config) {
        transformers.add(new ProcessEnvironmentTransformer(config.getBySection(PLUGIN_NAME)));
    }

    @Override
    public String getName() {
        return PLUGIN_NAME;
    }

    @Override
    public String getAuthor() {
        return "neo";
    }

    @Override
    public String getVersion() {
        return "v1.0.1";
    }

    @Override
    public String getDescription() {
        return "A plugin for the ja-netfilter, it can set custom env vars.";
    }

    @Override
    public List<MyTransformer> getTransformers() {
        return transformers;
    }
}
