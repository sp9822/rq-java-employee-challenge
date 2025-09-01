package com.reliaquest.api.client.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record Response<T>(T data, Status status, String error) {

    public static <T> Response<T> handled() {
        return new Response<>(null, Status.HANDLED, null);
    }

    public static <T> Response<T> handledWith(T data) {
        return new Response<>(data, Status.HANDLED, null);
    }

    public static <T> Response<T> error(String error) {
        return new Response<>(null, Status.ERROR, error);
    }

    public enum Status {
        HANDLED("Successfully processed request."),
        ERROR("Failed to process request.");

        @JsonValue
        @Getter
        private final String value;

        Status(String value) {
            this.value = value;
        }
    }
}
