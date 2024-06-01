package net.ashald.envfile.providers.mise;

import com.google.common.collect.ImmutableMap;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class MiseEnvVarsProviderTest {
    private static final MiseEnvVarsProvider PROVIDER = new MiseEnvVarsProvider();

    private static File getFile(String... names) {
        Path path = Paths.get("src", "test", "resources", "providers", "mise");
        for (val name : names) {
            path = path.resolve(name);
        }

        return new File(path.toString());
    }

    @Test
    @SneakyThrows
    public void GIVEN_getEnvVars_WHEN_defaultDir_THEN_correctPriority() {
        val result = PROVIDER.getEnvVars(getFile(), false, new HashMap<>());

        val entries = ImmutableMap.copyOf(result);

        ImmutableMap<String, String> expected = ImmutableMap.of(
                "D", "SHOULD_BE_PRINTED",
                "B", "SHOULD_BE_PRINTED",
                "C", "SHOULD_BE_PRINTED",
                "A", "MAY_BE_PRINTED"
        );

        assertEquals(expected, entries);
    }

    @Test
    @SneakyThrows
    public void GIVEN_getEnvVars_WHEN_innerPathFile_THEN_correctPriority() {
        val result = PROVIDER.getEnvVars(getFile("project", "Main.java"), false, new HashMap<>());

        val entries = ImmutableMap.copyOf(result);

        ImmutableMap<String, String> expected = ImmutableMap.of(
                "D", "SHOULD_BE_PRINTED",
                "B", "SHOULD_BE_PRINTED",
                "C", "SHOULD_BE_PRINTED",
                "A", "SHOULD_BE_PRINTED"
        );

        assertEquals(expected, entries);
    }
}
