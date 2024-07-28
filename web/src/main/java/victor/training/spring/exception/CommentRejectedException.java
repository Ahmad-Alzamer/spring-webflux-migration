package victor.training.spring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class CommentRejectedException extends BaseException{
    public CommentRejectedException() {
        super("Comment Rejected");
    }
}
