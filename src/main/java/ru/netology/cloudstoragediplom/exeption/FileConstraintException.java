package ru.netology.cloudstoragediplom.exeption;

public class FileConstraintException extends RuntimeException {
    private final int id = 103;

    public int getId() {
        return id;
    }

    public FileConstraintException(String message) {
        super(message);
    }
}
