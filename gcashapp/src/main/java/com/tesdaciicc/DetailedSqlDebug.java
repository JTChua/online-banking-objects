package com.tesdaciicc;

import java.io.*;
import java.util.stream.Collectors;

public class DetailedSqlDebug {

    public static void main(String[] args) {
        debugSqlFile();
    }
    
    private static void debugSqlFile() {
        System.out.println("=== Detailed SQL File Debug ===");
        
        try (InputStream in = DetailedSqlDebug.class.getResourceAsStream("/sql/003_data.sql");
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            
            if (in == null) {
                System.out.println("❌ SQL file not found!");
                return;
            }
            
            String content = reader.lines().collect(Collectors.joining("\n"));
            
            System.out.println("📄 Raw file content:");
            System.out.println("====================");
            System.out.println(content);
            System.out.println("====================");
            
            // Test the current splitting logic
            System.out.println("\n🔍 Testing current split logic:");
            String[] statements = content.split(";(?=(?:[^']*'[^']*')*[^']*$)");
            
            for (int i = 0; i < statements.length; i++) {
                String stmt = statements[i];
                System.out.println("\nStatement " + (i + 1) + ":");
                System.out.println("  Raw length: " + stmt.length());
                System.out.println("  Trimmed length: " + stmt.trim().length());
                System.out.println("  Starts with '--': " + stmt.trim().startsWith("--"));
                System.out.println("  Is empty after trim: " + stmt.trim().isEmpty());
                System.out.println("  First 50 chars: '" + stmt.substring(0, Math.min(50, stmt.length())) + "'");
            }
            
            // Test improved splitting
            System.out.println("\n🔧 Testing improved split logic:");
            String cleaned = cleanSqlForDebug(content);
            System.out.println("Cleaned content length: " + cleaned.length());
            
            String[] improvedStatements = cleaned.split(";\\s*(?=\\n|$)");
            for (int i = 0; i < improvedStatements.length; i++) {
                String stmt = improvedStatements[i].trim();
                if (!stmt.isEmpty() && !stmt.startsWith("--")) {
                    System.out.println("Valid statement " + (i + 1) + " (length: " + stmt.length() + "):");
                    System.out.println("  " + stmt.substring(0, Math.min(100, stmt.length())) + "...");
                }
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static String cleanSqlForDebug(String sql) {
        if (sql == null) return "";
        
        StringBuilder cleaned = new StringBuilder();
        String[] lines = sql.split("\n");
        
        for (String line : lines) {
            String trimmedLine = line.trim();
            
            // Skip empty lines and comment lines
            if (trimmedLine.isEmpty() || trimmedLine.startsWith("--")) {
                continue;
            }
            
            cleaned.append(line).append("\n");
        }
        
        return cleaned.toString().trim();
    }

}
