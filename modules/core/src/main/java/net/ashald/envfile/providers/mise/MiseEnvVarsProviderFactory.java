package net.ashald.envfile.providers.mise;

import net.ashald.envfile.EnvVarsProvider;
import net.ashald.envfile.EnvVarsProviderFactory;

import java.util.Map;
import java.util.function.Consumer;

public class MiseEnvVarsProviderFactory implements EnvVarsProviderFactory {
    @Override
    public EnvVarsProvider createProvider(Map<String, String> baseEnvVars, Consumer<String> logger) {
        return new MiseEnvVarsProvider();
    }

    @Override
    public String getTitle() {
        return "mise-en-place";
    }

    @Override
    public boolean isEditable() {
        return false;
    }
}
