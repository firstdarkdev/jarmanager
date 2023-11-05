/*
 * This file is part of JarManager, licensed under The MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.jarmanager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author HypherionSA
 * Internal task to perform the jar unpacking action. Should never be used directly.
 * Should be invoked from {@link JarManager#unpackJar(File, File)}
 */
final class JarUnpackTask {

    // JarManager instance
    private final JarManager manager;

    // List of unpacked files and directories that will be returned
    private final List<File> files = new ArrayList<>();

    // Files
    private final File inputJar;
    private final File outputDirectory;

    /**
     * Internal. Should not be used directly.
     * Create a new instance of the jar unpacker task
     * @param manager - Initialized instance of {@link JarManager}
     * @param inputJar - The input jar that will be unpacked
     * @param outputDirectory - The directory the jar will be unpacked to
     */
    JarUnpackTask(JarManager manager, File inputJar, File outputDirectory) {
        this.manager = manager;
        this.inputJar = inputJar;
        this.outputDirectory = outputDirectory;

        if (!inputJar.exists())
            throw new IllegalStateException("inputJar does not exist");

        if (!inputJar.isFile())
            throw new IllegalStateException("inputJar is not a file");
    }

    /**
     * Run the unpack task
     * @return - Returns a list of unpacked files and directories
     * @throws IOException - Thrown when an IO error occurs
     */
    List<File> unpackJar() throws IOException {
        // Open the input file for processing
        try (JarFile jarFile = new JarFile(inputJar)) {
            // Debug Logging
            if (manager.isDebugMode())
                System.out.println("Unpacking " + jarFile.getName() + "...");

            // Get a list of entries from the input jar
            Enumeration<JarEntry> enumEntries = jarFile.entries();

            // Create output directories
            if (!outputDirectory.exists()) {
                boolean created = outputDirectory.mkdirs();

                if (created && manager.isDebugMode())
                    System.out.println("Created output directory " + outputDirectory.getAbsolutePath());

                if (!created && manager.isDebugMode())
                    System.out.println("Failed to create output directory " + outputDirectory.getAbsolutePath());
            }

            // Process the entries
            while (enumEntries.hasMoreElements()) {
                JarEntry entry = enumEntries.nextElement();

                // Entry is a directory. Skip and continue
                if (entry.isDirectory())
                    continue;

                // The output file that will be written
                File outFile = new File(outputDirectory, entry.getName());

                // Debug Logging
                if (manager.isDebugMode()) {
                    System.out.println("[Unpacking] " + entry.getName() + " => " + outFile.getAbsolutePath());
                }

                // Create output directory for the file
                outFile.getParentFile().mkdirs();

                // Create the read and write streams
                InputStream inputStream = jarFile.getInputStream(entry);
                FileOutputStream outputStream = new FileOutputStream(outFile);

                // Initialize the buffer
                byte[] buffer = new byte[1024];
                int read;

                // Read the entry from the jar and write to the output directory
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }

                // Add the processed entry to the return list
                this.files.add(outFile);

                // Close everything
                outputStream.close();
                inputStream.close();
            }
        }

        return this.files;
    }

}
