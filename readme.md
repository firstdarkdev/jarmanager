## JarManager

JarManager is a java library that allows you un-pack jar files to a directory, pack directories to a jar and relocate packages inside a jar using [jar-relocator](https://github.com/lucko/jar-relocator).

The library is simple to use and doesn't require much configuration.

***

### Installation

Firstly, add our Maven to your `build.gradle` file.

```groovy
repositories {
    mavenCentral()
    
    // Your other repos might be here
    
    maven {
        url "https://maven.firstdarkdev.xyz/releases"
    }
}
```

Next, add the library to your `build.gradle` file.

![](https://maven.firstdarkdev.xyz/api/badge/latest/releases/com/hypherionmc/jarmanager?color=40c14a&name=jarmanager)

View the latest version on our [Maven](https://maven.firstdarkdev.xyz/#/releases/com/hypherionmc/jarmanager) or use the version in the badge above

```groovy
dependencies {
    // Existing dependencies
    implementation "com.hypherionmc:jarmanager:1.0.0"
}
```

***

### Using the library

Once you have the library added to your project, using it is really simple.

#### Unpacking a Jar file

```java
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
```

#### Repacking a Jar file

```java
public void repackJar() throws IOException {
        File testDirectory = new File("testdir");
        File inputDirectory = new File(testDirectory, "output");

        // Create a JarManager instance
        JarManager manager = JarManager.getInstance();

        // Pack the Jar
        manager.packJar(inputDirectory, new File(testDirectory, "output.jar"));
    }
```

#### Relocating Packages

```java
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
```

***

### Licenses

JarManager is licensed under the MIT License.

JarManager includes the following embedded libraries:

* [jar-relocator](https://github.com/firstdarkdev/jar-relocator) A fork of the original with additional features - Licensed under [Apache-2.0 License](https://github.com/lucko/jar-relocator/blob/master/LICENSE.txt)
* [OW2 ASM](https://gitlab.ow2.org/asm/asm) - Licensed under [BSD 3-Clause License](https://gitlab.ow2.org/asm/asm/-/blob/master/LICENSE.txt?ref_type=heads)