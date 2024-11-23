package ru.netology.cloudstoragediplom.exeption;

public class FileNotFoundException extends RuntimeException {
    private final int id = 666;

    public int getId() {
        return id;
    }

    public FileNotFoundException(String message) {
        super(message);
    }
}
