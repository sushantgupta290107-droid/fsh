package com.prizz.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;

// Syllabus: Lectures 49-51 - StreamTokenizer parsing text tokens from character stream
public class AuditTokenizer {

    public static void parseAndSummarizeAuditFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("[StreamTokenizer] Audit log file does not exist: " + filePath);
            return;
        }

        int wordCount = 0;
        int numberCount = 0;

        // Syllabus: Lectures 49-51 - Initializing StreamTokenizer with FileReader
        try (FileReader reader = new FileReader(file)) {
            StreamTokenizer tokenizer = new StreamTokenizer(reader);

            while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
                if (tokenizer.ttype == StreamTokenizer.TT_WORD) {
                    wordCount++;
                } else if (tokenizer.ttype == StreamTokenizer.TT_NUMBER) {
                    numberCount++;
                }
            }
        }

        System.out.println("[StreamTokenizer Analysis for " + file.getName() + "]");
        System.out.println(" • Total words identified: " + wordCount);
        System.out.println(" • Total numbers identified: " + numberCount);
    }
}
