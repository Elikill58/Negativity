package com.elikill58.negativity.universal.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.util.Formatter;

public class FileUtils {

	public static Path cleanDirectory(Path directory) throws IOException {
		if (!Files.isDirectory(directory)) {
			throw new NotDirectoryException(directory.toAbsolutePath().toString());
		}
		return Files.walkFileTree(directory, DirectoryCleaner.INSTANCE);
	}

	public static Path moveDirectory(Path originalDir, Path destinationDir) throws IOException {
		if (!Files.isDirectory(originalDir)) {
			throw new NotDirectoryException(originalDir.toAbsolutePath().toString());
		}

		if (Files.exists(destinationDir) && !Files.isDirectory(destinationDir)) {
			throw new NotDirectoryException(destinationDir.toAbsolutePath().toString());
		}

		return Files.walkFileTree(originalDir, new DirectoryMover(originalDir, destinationDir));
	}

	public static String getHashSHA256(File file) throws Exception {
		return getHash(file, "SHA-256");
	}

	public static String getHash(File file, String algo) throws Exception {
		MessageDigest messageDigest = MessageDigest.getInstance(algo);
		messageDigest.reset();
		messageDigest.update(Files.readAllBytes(file.toPath()));
		byte[] digest = messageDigest.digest();
		StringBuilder sb = new StringBuilder(digest.length * 2);
		try (Formatter formatter = new Formatter(sb)) {
			for (byte b : digest) {
				formatter.format("%02x", b);
			}
		}
		return sb.toString();
	}

	private static final class DirectoryCleaner extends SimpleFileVisitor<Path> {

		public static final DirectoryCleaner INSTANCE = new DirectoryCleaner();

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			Files.delete(file);
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
			Files.delete(dir);
			return FileVisitResult.CONTINUE;
		}
	}

	private static final class DirectoryMover extends SimpleFileVisitor<Path> {

		private final Path originalDir;
		private final Path destinationDir;

		private DirectoryMover(Path originalDir, Path destinationDir) {
			this.originalDir = originalDir;
			this.destinationDir = destinationDir;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			Path relativePath = originalDir.relativize(file);
			Path destinationPath = destinationDir.resolve(relativePath);
			Files.move(file, destinationPath, StandardCopyOption.REPLACE_EXISTING);
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			Path relativePath = originalDir.relativize(dir);
			Path destinationPath = destinationDir.resolve(relativePath);
			if (Files.notExists(destinationPath)) {
				Files.createDirectory(destinationPath);
			}
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
			Files.delete(dir);
			return FileVisitResult.CONTINUE;
		}
	}
}
