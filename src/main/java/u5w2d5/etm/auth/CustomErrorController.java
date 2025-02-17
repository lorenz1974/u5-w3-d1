package u5w2d5.etm.auth;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<Object> handleError(HttpServletRequest request) {
        RuntimeException exception = (RuntimeException) request.getAttribute("javax.servlet.error.exception");

        System.out.println("-----------Eccezione: " + exception);

        throw exception;
    }
}
