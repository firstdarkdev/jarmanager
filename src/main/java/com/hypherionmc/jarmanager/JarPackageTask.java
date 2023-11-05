/*
 * This file is part of JarManager, licensed under The MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.jarmanager;

import java.io.*;
import java.nio.file.Files;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

/**
 * @author HypherionSA
 * Internal task to perform the jar packing action. Should never be used directly.
 * Should be invoked from {@link JarManager#packJar(File, File)}
 */
final class JarPackageTask {

    // JarManager instance
    private final JarManager jarManager;

    // Output stream
    private JarOutputStream outputStream;

    // Files
    private final File inputDirectory;
    private final File outputJar;

    /**
     * Internal. Should not be used directly.
     * Create a new instance of the jar packager task
     * @param jarManager - Initialized instance of {@link JarManager}
     * @param inputDirectory The input directory that will be packed
     * @param outputJar - The output file the jar will be written to
     */
    JarPackageTask(JarManager jarManager, File inputDirectory, File outputJar) {
        this.jarManager = jarManager;
        this.inputDirectory = inputDirectory;
        this.outputJar = outputJar;

        if (!inputDirectory.exists())
            throw new IllegalStateException("inputDirectory does not exist");

        if (!inputDirectory.isDirectory())
            throw new IllegalStateException("inputDirectory is not a directory");

        if (outputJar == null)
            throw new IllegalStateException("outputJar cannot be null!");
    }

    /**
     * Run the package task
     * @return - The output jar that was packaged
     * @throws IOException - Thrown when an IO error occurs
     */
    File pack() throws IOException {
        // Set up the output stream and set the chosen compression level
        this.outputStream = new JarOutputStream(Files.newOutputStream(outputJar.toPath()));
        this.outputStream.setLevel(jarManager.getCompressionLevel());

        // Debug Logging
        if (jarManager.isDebugMode())
            System.out.println("Packing jar " + outputJar.getName());

        // Processes the folder to add all files and directories to the output jar
        this.addFilesRecursively(inputDirectory, null);

        // Debug Logging
        if (jarManager.isDebugMode())
            System.out.println("Packed jar " + outputJar.getName());

        // Close the output stream
        try {
            this.outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Return the new jar
        return outputJar;
    }

    /**
     * Add a file to the output jar
     * @param source - The file to be added
     * @param outputDir - The output directory. Used to fix file names in the output jar
     */
    private void addFile(File source, File outputDir) {
        // Input reader stream
        BufferedInputStream inputStream;

        try {
            // Get the correct file name for the final jar
            String sourcePath = source.getAbsolutePath().substring(outputDir.getAbsolutePath().length() + 1);

            // Source is a file. Process it
            if (!source.isDirectory()) {
                // Set up a new jar entry
                JarEntry entry = new JarEntry(sourcePath.replace("\\", "/"));
                entry.setTime(source.lastModified());
                this.outputStream.putNextEntry(entry);

                // Set up the input stream and buffer
                inputStream = new BufferedInputStream(Files.newInputStream(source.toPath()));
                byte[] buffer = new byte[1024];
                int count;

                // Read and write the file to the jar
                while ((count = inputStream.read(buffer)) != -1) {
                    this.outputStream.write(buffer, 0, count);
                }

                // Close the input/output stream
                this.outputStream.closeEntry();
                inputStream.close();
                return;
            }

            // Source is a directory. Add it to the jar, and process its files
            String name = sourcePath.replace("\\", "/");
            if (!name.isEmpty()) {
                // Add a trailing slash to the file name if there isn't one
                if (!name.endsWith("/")) {
                    name = name + "/";
                }

                // Write the jar entry to the jar
                JarEntry entry = new JarEntry(name);
                entry.setTime(source.lastModified());
                this.outputStream.putNextEntry(entry);

                // Close the entry
                this.outputStream.closeEntry();
            }

            // Loop over files in the directory
            File[] files = source.listFiles();
            if (files == null)
                return;

            for (File f : files) {
                // Add the files to the output jar
                this.addFile(f, outputDir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loop over files and folders in the inputDir to add them to the jar file
     * @param inputDir - The input directory containing the files that will be added to the output jar
     * @param outputDir - The directory holding the output. Used to fix file names in the output jar
     */
    private void addFilesRecursively(File inputDir, File outputDir) {
        if (outputDir == null)
            outputDir = inputDir;

        // Get a list of files and folders from the directory
        File[] files = inputDir.listFiles();
        if (files == null) {
            if (jarManager.isDebugMode())
                System.err.println("inputDirectory doesn't appear to contain files. Skipping...");
            return;
        }

        for (File f : files) {
            // It's a file. Add it
            if (f.isFile()) {
                this.addFile(f, outputDir);
                continue;
            }

            // It's a folder. Process it to find files, to add to the jar
            if (f.isDirectory())
                this.addFilesRecursively(f, outputDir);

        }
    }
}
