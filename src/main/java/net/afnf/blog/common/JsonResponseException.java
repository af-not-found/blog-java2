package net.afnf.blog.common;

@SuppressWarnings("serial")
public class JsonResponseException extends RuntimeException {

    private String message;

    public JsonResponseException() {

    }

    public JsonResponseException(String message) {
        this.message = message;
    }

    public String toString() {
        return "JsonResponseException, msg=" + message;
    };
}
