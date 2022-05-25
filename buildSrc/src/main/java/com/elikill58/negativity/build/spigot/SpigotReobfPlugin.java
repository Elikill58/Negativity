package com.elikill58.negativity.build.spigot;

import java.io.File;
import java.nio.file.Path;

import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.gradle.jvm.tasks.Jar;

public class SpigotReobfPlugin implements Plugin<Project> {
	
	public static final String CONFIG_SPIGOT_OBF_MOJANG = "spigotObfMojang";
	public static final String CONFIG_SPIGOT_OBF = "spigotObf";
	public static final String CONFIG_REOBF_MAP_MOJANG = "reobfMapMojang";
	public static final String CONFIG_REOBF_MAP_SPIGOT = "reobfMapSpigot";
	public static final String CONFIG_SPECIAL_SOURCE = "specialSource";

	public static final String TASK_SHADOW_JAR = "shadowJar";
	public static final String TASK_REOBF_MOJMAP = "reobfMojmap";
	public static final String TASK_REOBF_SPIGOT = "reobfSpigot";
	
	@Override
	public void apply(Project project) {
		// Setup configurations
		ConfigurationContainer configurations = project.getConfigurations();
		NamedDomainObjectProvider<Configuration> configSpigotObfMojang = configurations.register(CONFIG_SPIGOT_OBF_MOJANG);
		configurations.named(JavaPlugin.COMPILE_CLASSPATH_CONFIGURATION_NAME).configure(configuration -> {
			configuration.extendsFrom(configurations.getByName(CONFIG_SPIGOT_OBF_MOJANG));
		});
		NamedDomainObjectProvider<Configuration> configSpigotObf = configurations.register(CONFIG_SPIGOT_OBF);
		
		NamedDomainObjectProvider<Configuration> configReobfMapMojang = configurations.register(CONFIG_REOBF_MAP_MOJANG);
		NamedDomainObjectProvider<Configuration> configReobfMapSpigot = configurations.register(CONFIG_REOBF_MAP_SPIGOT);
		
		NamedDomainObjectProvider<Configuration> configSpecialSource = configurations.register(CONFIG_SPECIAL_SOURCE);
		
		// Configure jar and shadowJar tasks
		TaskContainer tasks = project.getTasks();
		tasks.named(JavaPlugin.JAR_TASK_NAME, Jar.class)
			.configure(jarTask -> jarTask.getArchiveClassifier().convention("dev"));
		
		TaskProvider<AbstractArchiveTask> shadowJar = tasks.named(TASK_SHADOW_JAR, AbstractArchiveTask.class);
		shadowJar.configure(shadowTask -> shadowTask.getArchiveClassifier().set("all-dev"));
		
		// Configure remap tasks
		TaskProvider<JavaExec> reobfMojmap = tasks.register(TASK_REOBF_MOJMAP, JavaExec.class, task -> {
			task.dependsOn(shadowJar);
			
			task.classpath(configSpecialSource, configSpigotObfMojang);
			task.getMainClass().set("net.md_5.specialsource.SpecialSource");
			task.workingDir(task.getTemporaryDir());
			
			File mapFile = configReobfMapMojang.get().getSingleFile();
			File inputFile = shadowJar.get().getOutputs().getFiles().getSingleFile();
			task.getInputs().files(mapFile, inputFile);
			
			String outputFileName = "moj_" + inputFile.getName();
			Path outputPath = task.getTemporaryDir().toPath().resolve(outputFileName);
			task.getOutputs().file(outputPath);
			
			task.args("--live", "--quiet", "-i", inputFile.getAbsolutePath(), "-o", outputPath.toAbsolutePath(), "-m", mapFile.getAbsolutePath(), "--reverse");
		});
		tasks.register(TASK_REOBF_SPIGOT, JavaExec.class, task -> {
			task.dependsOn(reobfMojmap);
			
			task.classpath(configSpecialSource, configSpigotObf);
			task.getMainClass().set("net.md_5.specialsource.SpecialSource");
			task.workingDir(task.getTemporaryDir());
			
			File mapFile = configReobfMapSpigot.get().getSingleFile();
			File inputFile = reobfMojmap.get().getOutputs().getFiles().getSingleFile();
			task.getInputs().files(mapFile, inputFile);
			
			String outputFileName = buildReobfJarName(shadowJar.get());
			
			Path outputPath = project.getLayout().getBuildDirectory().get().getAsFile().toPath().resolve("libs/" + outputFileName);
			task.getOutputs().file(outputPath);
			
			task.args("--live", "--quiet", "-i", inputFile.getAbsolutePath(), "-o", outputPath.toAbsolutePath(), "-m", mapFile.getAbsolutePath());
		});
	}
	
	private static String buildReobfJarName(AbstractArchiveTask archiveTask) {
		String name = archiveTask.getArchiveBaseName().getOrNull();
		if (name == null) {
			name = archiveTask.getProject().getName();
		}
		
		String appendix = archiveTask.getArchiveAppendix().getOrNull();
		if (appendix != null) {
			name += "-" + appendix;
		}
		
		String version = archiveTask.getArchiveVersion().getOrNull();
		if (version != null) {
			name += "-" + version;
		}
		
		name += ".jar";
		
		return name;
	}
	
	public static void configureSpigotRemapDeps(Project project, String spigotVersion, String specialSourceVersion) {
		DependencyHandler deps = project.getDependencies();
		deps.add(CONFIG_SPIGOT_OBF_MOJANG, "org.spigotmc:spigot:%s:remapped-mojang".formatted(spigotVersion));
		deps.add(CONFIG_SPIGOT_OBF, "org.spigotmc:spigot:%s:remapped-obf".formatted(spigotVersion));
		deps.add(CONFIG_REOBF_MAP_MOJANG, "org.spigotmc:minecraft-server:%s:maps-mojang@txt".formatted(spigotVersion));
		deps.add(CONFIG_REOBF_MAP_SPIGOT, "org.spigotmc:minecraft-server:%s:maps-spigot@csrg".formatted(spigotVersion));
		deps.add(CONFIG_SPECIAL_SOURCE, "net.md-5:SpecialSource:%s".formatted(specialSourceVersion));
	}
}
