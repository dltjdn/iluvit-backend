package FIS.iLUVit.exception.exceptionHandler.controllerAdvice;

import FIS.iLUVit.exception.exceptionHandler.ValidationErrorResult;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationControllerAdvice {

    @ExceptionHandler
    public ValidationErrorResult methodValidException(MethodArgumentNotValidException e){
        ValidationErrorResult validationErrorResult = new ValidationErrorResult();
        BindingResult bindingResult = e.getBindingResult();
        bindingResult.getAllErrors()
                        .forEach(error -> {
                            validationErrorResult.getMessages()
                                    .add(error.getDefaultMessage());
                        });
        validationErrorResult.setCode("Request Bad");
        return validationErrorResult;
    }
}
