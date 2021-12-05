package com.elikill58.negativity.universal.file;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileHandle {

	private static final List<FileHandle> FILE_HANDLES = new ArrayList<>();
	public static List<FileHandle> getFileHandles() {
		return FILE_HANDLES;
	}
	private static final long MAX_TIME = 10000; // 1000 = 1 second
	private final BufferedWriter handle;
	private boolean closed = false;
	private long lastUpdate;
	
	public FileHandle(Path file) throws IOException {
		this.handle = Files.newBufferedWriter(file, StandardOpenOption.APPEND);
		this.lastUpdate = System.currentTimeMillis();
		FILE_HANDLES.add(this);
	}
	
	public void update() {
		this.lastUpdate = System.currentTimeMillis();
	}
	
	public boolean isClosed() {
		return handle == null || closed;
	}
	
	public void write(List<String> lines) throws IOException {
		for(String s : lines)
			handle.append(s + "\n");
	}
	
	public boolean shouldBeClosed() {
		return !isClosed() && System.currentTimeMillis() - lastUpdate > MAX_TIME;
	}
	
	public void close() {
		closed = true;
		try {
			handle.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		FILE_HANDLES.remove(this);
	}
}
