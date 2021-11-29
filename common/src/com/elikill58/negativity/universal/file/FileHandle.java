package com.elikill58.negativity.universal.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileHandle {

	private static final List<FileHandle> FILE_HANDLES = new ArrayList<>();
	public static List<FileHandle> getFileHandles() {
		return FILE_HANDLES;
	}
	private static final long MAX_TIME = 10000; // 1000 = 1 second
	private final FileWriter handle;
	private boolean closed = false;
	private long lastUpdate;
	
	public FileHandle(File file) throws IOException {
		this.handle = new FileWriter(file, true);
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
