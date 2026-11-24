package com.prizz.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

// Syllabus: Lectures 49-51 - Byte streams: FileInputStream and FileOutputStream
// Syllabus: Lectures 52-54 - Exception-safe resource handling using try-with-resources
public class ByteStreamBackup {

    // Syllabus: Lectures 49-51 - Writing raw bytes using FileOutputStream
    public static void createBinaryBackup(String filePath, String content) throws IOException {
        File file = new File(filePath);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        byte[] data = content.getBytes();
        // Syllabus: Lectures 49-51 & 52-54 - FileOutputStream inside try-with-resources
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
            fos.flush();
        }
        System.out.println("[ByteStream] Wrote " + data.length + " bytes to binary backup: " + filePath);
    }

    // Syllabus: Lectures 49-51 - Reading raw bytes using FileInputStream
    public static String readBinaryBackup(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        // Syllabus: Lectures 49-51 & 52-54 - FileInputStream inside try-with-resources
        try (FileInputStream fis = new FileInputStream(file)) {
            int byteRead;
            while ((byteRead = fis.read()) != -1) {
                sb.append((char) byteRead);
            }
        }
        System.out.println("[ByteStream] Read " + sb.length() + " bytes from: " + filePath);
        return sb.toString();
    }
}
