package com.catalog.utils.server;

import com.catalog.utils.errors.SimpleError;
import com.catalog.utils.errors.ValidationError;
import com.mongodb.lang.NonNull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import java.io.IOException;

@Component
public class RestResponseStatusExceptionResolver extends AbstractHandlerExceptionResolver {
    @Autowired
    CatalogLogger logger;

    @Override
    protected ModelAndView doResolveException(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            Object handler,
            @NonNull Exception ex
    ) {
        try {
            if (ex instanceof SimpleError) {
                return handleSimpleError((SimpleError) ex, response, handler);
            } else if (ex instanceof ValidationError) {
                return handleValidationError((ValidationError) ex, response, handler);
            }
        } catch (Exception handlerException) {
            logger.info(handlerException.getMessage());
        }
        return null;
    }

    private ModelAndView handleValidationError(ValidationError ex, HttpServletResponse response, Object handler) throws IOException {
        response.sendError(ex.statusCode(), ex.toJson());
        return new ModelAndView();
    }

    private ModelAndView handleSimpleError(SimpleError ex, HttpServletResponse response, Object handler) throws IOException {
        response.sendError(ex.statusCode(), ex.toJson());
        return new ModelAndView();
    }
}