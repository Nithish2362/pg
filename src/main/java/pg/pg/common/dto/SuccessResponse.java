package pg.pg.common.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class SuccessResponse extends ResponseEntity<Object> {

    public SuccessResponse(final Object payload) {
        this(null, payload, null);
    }

    public SuccessResponse(final String message) {
        this(message, null, null);
    }

    public SuccessResponse(final String message, final Object payload) {
        this(message, payload, null);
    }

    public SuccessResponse(final String message, final Object payload, final Long totalCount) {
        super(new SuccessPayload(message, payload, totalCount), HttpStatus.OK);
    }
}
