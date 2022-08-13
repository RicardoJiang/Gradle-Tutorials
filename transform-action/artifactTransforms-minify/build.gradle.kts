import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

plugins {
    `java-library`
}

abstract class Minify : TransformAction<Minify.Parameters> {   // <1>
    interface Parameters : TransformParameters {               // <2>
        @get:Input
        var keepClassesByArtifact: Map<String, Set<String>>

        @get:Input
        var timestamp: Long
    }

    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    @get:InputArtifact
    abstract val inputArtifact: Provider<FileSystemLocation>

    override
    fun transform(outputs: TransformOutputs) {
        val fileName = inputArtifact.get().asFile.name
        for (entry in parameters.keepClassesByArtifact) {      // <3>
            if (fileName.startsWith(entry.key)) {
                val nameWithoutExtension = fileName.substring(0, fileName.length - 4)
                minify(
                    inputArtifact.get().asFile,
                    entry.value,
                    outputs.file("${nameWithoutExtension}-min.jar")
                )
                return
            }
        }
        println("Nothing to minify - using ${fileName} unchanged")
        outputs.file(inputArtifact)                            // <4>
    }

    private fun minify(artifact: File, keepClasses: Set<String>, jarFile: File) {
        println("Minifying ${artifact.name}")
        // Implementation ...
// end::artifact-transform-minify[]
        JarOutputStream(BufferedOutputStream(FileOutputStream(jarFile))).use { jarOutputStream ->
            ZipFile(artifact).use { zip ->
                for (entry in zip.entries()) {
                    if (entry.isDirectory) {
                        jarOutputStream.putNextEntry(ZipEntry(entry.name))
                        jarOutputStream.closeEntry()
                    } else if (entry.name.endsWith(".class")) {
                        val className =
                            entry.name.replace("/", ".").substring(0, entry.name.length - 6)
                        if (keepClasses.contains(className)) {
                            jarOutputStream.addEntry(entry, zip)
                        }
                    } else {
                        jarOutputStream.addEntry(entry, zip)
                    }
                }
            }
        }
    }

    private fun JarOutputStream.addEntry(entry: ZipEntry, zip: ZipFile) {
        putNextEntry(ZipEntry(entry.name))
        zip.getInputStream(entry).use { it.copyTo(this) }
        closeEntry()
// tag::artifact-transform-minify[]
    }
}

val artifactType = Attribute.of("artifactType", String::class.java)
val minified = Attribute.of("minified", Boolean::class.javaObjectType)

val keepPatterns = mapOf(
    "guava" to setOf(
        "com.google.common.base.Optional",
        "com.google.common.base.AbstractIterator"
    )
)

dependencies {
    attributesSchema {
        attribute(minified)                      // <1>
    }
    artifactTypes.getByName("jar") {
        attributes.attribute(minified, false)    // <2>
    }
}

configurations.all {
    afterEvaluate {
        if (isCanBeResolved) {
            attributes.attribute(minified, true) // <3>
        }
    }
}

dependencies {
    registerTransform(Minify::class) {
        from.attribute(minified, false).attribute(artifactType, "jar")
        to.attribute(minified, true).attribute(artifactType, "jar")

        parameters {
            keepClassesByArtifact = keepPatterns
            // Make sure the transform executes each time
            timestamp = System.nanoTime()
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {                                 // <4>
    implementation("com.google.guava:guava:27.1-jre")
    implementation(project(":producer"))
}

tasks.register<Copy>("resolveRuntimeClasspath") {
    from(configurations.runtimeClasspath)
    into(layout.buildDirectory.dir("runtimeClasspath"))
}
