package de.fraunhofer.abm.projectanalyzer.api;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Convenience class, so that you don't have to implement all methods.
 * Makes the code cleaner, too.
 *
 * {@inheritDoc}
 */
public abstract class FileVisitorAdapter<T> implements FileVisitor<T> {

    @Override
    public FileVisitResult preVisitDirectory(T dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(T dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(T dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(T dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }
}
