package com.elikill58.negativity.build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.file.FileVisitor;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskExecutionException;
import org.gradle.api.tasks.TaskProvider;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class InstrumentClassesTask extends DefaultTask {
	
	private final ConfigurableFileCollection inputClassesDirs;
	private final DirectoryProperty outputDir;
	private List<BytecodeTransformerFactory> transformers = new ArrayList<>();
	
	@Inject
	public InstrumentClassesTask(ObjectFactory fileCollectionFactory) {
		this.inputClassesDirs = fileCollectionFactory.fileCollection();
		this.outputDir = fileCollectionFactory.directoryProperty();
		this.transformers.add(new EventListenerTransformer.Factory());
	}
	
	@SkipWhenEmpty
	@InputFiles
	@PathSensitive(PathSensitivity.RELATIVE)
	public ConfigurableFileCollection getInputClassesDirs() {
		return this.inputClassesDirs;
	}
	
	@OutputDirectory
	public DirectoryProperty getOutputDir() {
		return this.outputDir;
	}
	
	@Internal
	public List<BytecodeTransformerFactory> getTransformers() {
		return this.transformers;
	}
	
	public void setTransformers(List<BytecodeTransformerFactory> transformers) {
		this.transformers = transformers;
	}
	
	public void transformer(BytecodeTransformerFactory transformer) {
		this.transformers.add(transformer);
	}
	
	public void transformers(BytecodeTransformerFactory... transformers) {
		Collections.addAll(this.transformers, transformers);
	}
	
	@TaskAction
	public void instrumentClasses() {
		System.out.println(this.transformers);
		this.inputClassesDirs.getAsFileTree().visit(new InstrumentingFileVisitor());
	}
	
	public static void setupInstrumentation(Project project, SourceSet sourceSet) {
		TaskContainer tasks = project.getTasks();
		String instrumentTaskName = sourceSet.getTaskName("instrument", "classes");
		TaskProvider<InstrumentClassesTask> instrumentTask = tasks.register(instrumentTaskName, InstrumentClassesTask.class, task -> {
			task.dependsOn(sourceSet.getClassesTaskName());
			Set<File> classesDirs = sourceSet.getOutput().getClassesDirs().getFiles();
			task.getInputClassesDirs().setFrom(classesDirs);
			File instrumentedOutputDir = new File(classesDirs.toArray(new File[0])[0].getParentFile(), sourceSet.getName() + "-instrumented");
			task.getOutputDir().convention(project.getLayout().getProjectDirectory().dir(instrumentedOutputDir.getPath()));
		});
		String postInstrumentTaskName = "post" + capitalize(instrumentTaskName);
		TaskProvider<Task> postInstrumentTask = tasks.register(postInstrumentTaskName, task -> {
			task.dependsOn(instrumentTask);
			DirectoryProperty instrumentationOutput = instrumentTask.get().getOutputDir();
			ConfigurableFileCollection classesDirs = (ConfigurableFileCollection) sourceSet.getOutput().getClassesDirs();
			task.doLast(t -> classesDirs.setFrom(instrumentationOutput));
		});
		sourceSet.compiledBy(postInstrumentTask);
	}
	
	private static String capitalize(String instrumentTaskName) {
		return Character.toUpperCase(instrumentTaskName.charAt(0)) + instrumentTaskName.substring(1);
	}
	
	private class InstrumentingFileVisitor implements FileVisitor {
		
		private final File baseOutputDir = InstrumentClassesTask.this.outputDir.getAsFile().get();
		
		@Override
		public void visitDir(FileVisitDetails dirDetails) {
			dirDetails.getRelativePath().getFile(baseOutputDir).mkdirs();
		}
		
		@Override
		public void visitFile(FileVisitDetails fileDetails) {
			int api = Opcodes.ASM9;
			try (InputStream inputStream = fileDetails.open()) {
				ClassReader classReader = new ClassReader(inputStream);
				ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
				ClassVisitor visitor = classWriter;
				for (BytecodeTransformerFactory transformer : InstrumentClassesTask.this.getTransformers()) {
					visitor = transformer.transform(InstrumentClassesTask.this, api, visitor);
				}
				
				classReader.accept(visitor, ClassReader.SKIP_FRAMES);
				byte[] classOutput = classWriter.toByteArray();
				File outputFile = fileDetails.getRelativePath().getFile(baseOutputDir);
				try (FileOutputStream fileWriter = new FileOutputStream(outputFile)) {
					fileWriter.write(classOutput);
				}
			} catch (IOException e) {
				throw new TaskExecutionException(InstrumentClassesTask.this, e);
			}
		}
	}
}
