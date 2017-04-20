package de.fraunhofer.abm.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class FileUtil {

    public static void deleteRecursively(File dir) throws IOException {
        if(!dir.exists()) {
            return;
        }

        removeReadonlyFlag(dir);
        Path directory = dir.toPath();
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
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

        });
    }

    public static void removeReadonlyFlag(File dir) throws IOException {
        if(!dir.exists()) {
            return;
        }

        Path directory = dir.toPath();
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                file.toFile().setWritable(true);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                dir.toFile().setWritable(true);
                return FileVisitResult.CONTINUE;
            }

        });
    }

    public static void copy(File source, File target) throws IOException {
        Files.walkFileTree(source.toPath(), new CopyFileVisitor(target.toPath()));
    }
}
