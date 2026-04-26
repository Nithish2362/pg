package pg.pg.dto;

import lombok.Getter;

@Getter
public class SuccessPayload {
    private String message;
    private Object response;
    private Long count;

    public SuccessPayload(final String message, final Object payload, final Long count) {
        this.message = message;
        this.response = payload;
        this.count = count;
    }
}
