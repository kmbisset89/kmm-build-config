package io.github.kmbisset89.kmpbuildconfig.plugin

import io.github.kmbisset89.kmpbuildconfig.plugin.logic.WriteBuildConfigFileUseCase
import org.gradle.api.DefaultTask
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option


/**
 * A Gradle task that generates a BuildConfig Kotlin file for a project. This class allows setting various
 * properties such as version, package name, and other custom properties through Gradle command-line options
 * or directly in the build script. It defaults to generating the BuildConfig in the `commonMain` source set
 * but can be overridden to target a different source set.
 *
 * The generated BuildConfig file will include a VERSION constant with the specified version, along with any
 * additional properties provided through the `propertyMap`.
 */
abstract class MakeBuildConfig : DefaultTask() {

    init {
        // Describes the task and assigns it to the build group for organization in Gradle's task list.
        description = "Creates a build config file for the project in commonMain, unless an override is provided."
        group = BasePlugin.BUILD_GROUP
    }

    // Version property to be included in the BuildConfig.
    @get:Input
    @get:Option(
        option = "version",
        description = "The version to set for the project in the build config file."
    )
    abstract val version: Property<String>

    // Package name for the generated BuildConfig file.
    @get:Input
    @get:Option(
        option = "packageName",
        description = "The package name to set for the project in the build config file."
    )
    abstract val packageName: Property<String>

    // Optional source set name where the BuildConfig will be generated.
    @get:Input
    @get:Optional
    @get:Option(
        option = "sourceSetName",
        description = "The source set name to set for the project in the build config file. Defaults to commonMain."
    )
    abstract val sourceSetName: Property<String?>

    // Optional file name for the BuildConfig, defaulting to BuildConfig.kt.
    @get:Input
    @get:Optional
    @get:Option(
        option = "buildConfigFileName",
        description = "The name of the build config file to create. Defaults to BuildConfig.kt."
    )
    abstract val buildConfigFileName: Property<String?>

    // Optional map of additional properties to include in the BuildConfig.
    @get:Input
    @get:Optional
    @get:Option(
        option = "propertyMap",
        description = "A map of properties to set in the build config file."
    )
    abstract val propertyMap: Property<Map<String, String>?>

    /**
     * The task's action to execute the build config file generation.
     * It retrieves all the properties set either through Gradle's command-line options
     * or specified in the project's build script and passes them to the [WriteBuildConfigFileUseCase].
     */
    @TaskAction
    fun executeTask() {
        // Retrieve properties or their default values.
        val version = version.get()
        val packageName = packageName.get()
        val sourceSetName = sourceSetName.orNull ?: "commonMain"
        val buildConfigFileName = buildConfigFileName.orNull ?: "BuildConfig.kt"
        val propertyMap = propertyMap.orNull ?: emptyMap()

        // Execute the use case to generate the BuildConfig file.
        WriteBuildConfigFileUseCase().invoke(
            version,
            packageName,
            sourceSetName,
            buildConfigFileName,
            propertyMap,
            project
        )
    }
}