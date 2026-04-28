package pg.pg.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class InvalidDataException extends RuntimeException {


    public InvalidDataException(){

    }

    public InvalidDataException(final String message) {
        super(message);
    }

    public InvalidDataException(String message , Throwable msg){

        super(message,msg);
    }
}