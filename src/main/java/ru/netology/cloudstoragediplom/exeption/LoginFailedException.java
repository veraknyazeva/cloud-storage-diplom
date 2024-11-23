package ru.netology.cloudstoragediplom.exeption;

public class LoginFailedException extends RuntimeException {
    private final int id = 999;

    public int getId() {
        return id;
    }

    public LoginFailedException(String message) {
        super(message);
    }
}
