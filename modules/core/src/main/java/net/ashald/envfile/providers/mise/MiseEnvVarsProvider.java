package net.ashald.envfile.providers.mise;

import com.moandjiezana.toml.Toml;
import lombok.val;
import net.ashald.envfile.EnvVarsProvider;
import net.ashald.envfile.exceptions.EnvFileException;
import net.ashald.envfile.exceptions.InvalidEnvFileException;
import net.ashald.envfile.providers.EnvFileReader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MiseEnvVarsProvider implements EnvVarsProvider {

    @Override
    public Map<String, String> getEnvVars(
            File file,
            boolean isExecutable,
            Map<String, String> context
    ) throws EnvFileException {
        if (file == null) {
            throw InvalidEnvFileException.format(
                    "%s is supposed to be configured with a file",
                    this.getClass().getSimpleName()
            );
        }

        if (isExecutable) {
            throw InvalidEnvFileException.format(
                    "%s does not support executable mode",
                    this.getClass().getSimpleName()
            );
        }

        File baseDir = file;
        if (!baseDir.exists() || baseDir.isFile()) {
            baseDir = baseDir.getParentFile();
        }

        return getEnvVarsFrom(baseDir, System.getenv("MISE_ENV"));
    }

    private static Map<String, String> getEnvVarsFrom(@NotNull File baseDir, String miseEnv) throws EnvFileException {
        val result = new HashMap<String, String>();

        val parent = baseDir.getParentFile();
        if (parent != null) {
            result.putAll(getEnvVarsFrom(parent, miseEnv));
        }

        for (val envFile : getEnvFilesFrom(baseDir, miseEnv)) {
            val content = EnvFileReader.DEFAULT.read(envFile);
            val envTable = new Toml()
                    .read(content)
                    .getTable("env");

            if (envTable == null) {
                continue;
            }

            for (val entry : envTable.entrySet()) {
                result.put(entry.getKey(), entry.getValue().toString());
            }
        }

        return result;
    }

    // NOTE: The list of file paths is from https://mise.jdx.dev/profiles.html
    private static List<File> getEnvFilesFrom(@NotNull File dir, String miseEnv) {
        val result = Arrays.asList(
                new File(dir, ".config/mise/config.toml"),
                new File(dir, "mise/config.toml"),
                new File(dir, "mise.toml"),
                new File(dir, ".mise/config.toml"),
                new File(dir, ".mise.toml"),
                new File(dir, ".config/mise/config.local.toml"),
                new File(dir, "mise/config.local.toml"),
                new File(dir, "mise.local.toml"),
                new File(dir, ".mise/config.local.toml"),
                new File(dir, ".mise.local.toml")
        );

        if (miseEnv != null) {
            result.add(new File(dir, String.format(".config/mise/config.%s.toml", miseEnv)));
            result.add(new File(dir, String.format("mise/config.%s.toml", miseEnv)));
            result.add(new File(dir, String.format("mise.%s.toml", miseEnv)));
            result.add(new File(dir, String.format(".mise/config.%s.toml", miseEnv)));
            result.add(new File(dir, String.format(".mise.%s.toml", miseEnv)));
            result.add(new File(dir, String.format(".config/mise/config.%s.local.toml", miseEnv)));
            result.add(new File(dir, String.format("mise/config.%s.local.toml", miseEnv)));
            result.add(new File(dir, String.format(".mise/config.%s.local.toml", miseEnv)));
            result.add(new File(dir, String.format(".mise.%s.local.toml", miseEnv)));
        }

        return result.stream().filter(File::exists).collect(Collectors.toList());
    }
}
