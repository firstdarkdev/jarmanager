import com.hypherionmc.jarmanager.JarManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.Deflater;

public class JarTests {

    private static final File testDirectory = new File("testdir");
    private static final File outputDirectory = new File(testDirectory, "output");

    public void unpackJar() throws IOException {
        File testDirectory = new File("testdir");
        File outputDirectory = new File(testDirectory, "output");

        if (!outputDirectory.exists())
            outputDirectory.mkdirs();

        // Create a JarManager instance
        JarManager manager = JarManager.getInstance();

        // Unpack the Jar
        manager.unpackJar(new File(testDirectory, "input.jar"), outputDirectory);
    }

    public void repackJar() throws IOException {
        File testDirectory = new File("testdir");
        File inputDirectory = new File(testDirectory, "output");

        // Create a JarManager instance
        JarManager manager = JarManager.getInstance();

        // Unpack the Jar
        manager.packJar(inputDirectory, new File(testDirectory, "output.jar"));
    }

    public void remapJar() throws IOException {
        File testDirectory = new File("testdir");
        File inputFile = new File(testDirectory, "input.jar");

        // Create a JarManager instance
        JarManager manager = JarManager.getInstance();

        // Remap the Jar
        HashMap<String, String> rl = new HashMap<>();
        rl.put("com.gitlab.cdagaming", "test.com.gitlab.cdagaming");
        manager.remapJar(inputFile, new File(testDirectory, "remapped.jar"), rl);
    }

    public static void main(String[] args) throws IOException {
        if (!outputDirectory.exists())
            outputDirectory.mkdirs();

        File inputJar = new File(testDirectory, "forge.jar");
        File outputJar = new File(testDirectory, "forge-output.jar");

        // Unpack Jar
        JarManager manager = JarManager.getInstance();
        manager.unpackJar(inputJar, outputDirectory);

        // Repack jar with best compression
        manager.setCompressionLevel(Deflater.BEST_COMPRESSION);
        manager.packJar(outputDirectory, outputJar);

        // Relocation Test
        HashMap<String, String> rl = new HashMap<>();
        rl.put("com.gitlab.cdagaming", "test.com.gitlab.cdagaming");
        manager.remapJar(outputJar, new File(testDirectory, "outputJar.jar"), rl);
    }

}
