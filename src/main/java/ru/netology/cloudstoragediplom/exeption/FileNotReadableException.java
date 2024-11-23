package ru.netology.cloudstoragediplom.exeption;

public class FileNotReadableException extends RuntimeException {
    private final int id = 555;

    public int getId() {
        return id;
    }

    public FileNotReadableException(String message) {
        super(message);
    }

}
