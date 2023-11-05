/*
 * This file is part of JarManager, licensed under The MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.jarmanager;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.lucko.jarrelocator.JarRelocator;
import me.lucko.jarrelocator.Relocation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Deflater;

/**
 * @author HypherionSA
 * Main JarManager class. Allows you to enable debugging, set the compression level and is a central place to run jar tasks
 */
@NoArgsConstructor(staticName = "getInstance")
public final class JarManager {

    /**
     * Enable additional debug logging
     */
    @Getter
    @Setter
    private boolean debugMode = false;

    /**
     * Set the compression level for the output jar
     */
    @Getter
    @Setter
    private int compressionLevel = Deflater.DEFAULT_COMPRESSION;

    /**
     * Pack a directory into a jar file
     * @param inputDirectory - The directory containing the content for the jar
     * @param outputJar - The file that the jar will be written to
     * @return - The packaged jar file
     * @throws IOException - Thrown when an IO error occurs
     */
    public File packJar(File inputDirectory, File outputJar) throws IOException {
        JarPackageTask task = new JarPackageTask(this, inputDirectory, outputJar);
        return task.pack();
    }

    /**
     * Unpack a jar file into a directory
     * @param inputJar - The input jar file to be unpacked
     * @param outputDirectory - The directory the jar file will be unpacked to
     * @return - A list of unpacked files and directories
     * @throws IOException - Thrown when an IO error occurs
     */
    public List<File> unpackJar(File inputJar, File outputDirectory) throws IOException {
        JarUnpackTask task = new JarUnpackTask(this, inputJar, outputDirectory);
        return task.unpackJar();
    }

    /**
     * Relocate or remap packages inside a jar file.
     * For example, from com.google.gson to lib.com.google.gson
     * @param inputJar - The input jar file that will be modified
     * @param outputJar - The file the modified file will be saved to
     * @param relocations - Packages to relocate. See example above
     * @throws IOException - Thrown when an IO error occurs
     */
    public void remapJar(File inputJar, File outputJar, HashMap<String, String> relocations) throws IOException {
        // Set up temp file, to prevent accidental overwrites
        File tempJar = new File(inputJar.getParentFile(), "tempRelocated.jar");

        // Run the jar relocater task
        JarRelocator relocator = new JarRelocator(inputJar, tempJar, relocations);
        relocator.run();

        // Move the temporary file to the outputJar
        Files.move(tempJar.toPath(), outputJar.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Relocate or remap packages inside a jar file.
     * For example, from com.google.gson to lib.com.google.gson
     * @param inputJar - The input jar file that will be modified
     * @param outputJar - The file the modified file will be saved to
     * @param relocations - Packages to relocate. See example above
     * @throws IOException - Thrown when an IO error occurs
     */
    public void remapJar(File inputJar, File outputJar, List<Relocation> relocations) throws IOException {
        // Set up temp file, to prevent accidental overwrites
        File tempJar = new File(inputJar.getParentFile(), "tempRelocated.jar");

        // Run the jar relocater task
        JarRelocator relocator = new JarRelocator(inputJar, tempJar, relocations);
        relocator.run();

        // Move the temporary file to the outputJar
        Files.move(tempJar.toPath(), outputJar.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}
