package com.project.exception;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.project.bean.vo.AjaxResult;
import com.project.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 捕捉所有运行时异常.
 */
public class ExceptionHandler implements HandlerExceptionResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (!(ex instanceof RuntimeException)) {
            return null;
        }
        LOGGER.error( ex.getMessage(),ex);
        if (isAcceptJson(request)) {
            Map<String, Object> map = new HashMap<>();
            map.put("status", Constants.HTTP_500);
            map.put("message", ex.getMessage());
            ModelAndView modelAndView = new ModelAndView(new FastJsonJsonView(),map);
            return modelAndView;
        }
        Map<String,Object> map=new HashMap<>();
        map.put("exception",ex);
        return new ModelAndView("exception/runtimeException",map);
    }

    protected boolean isAcceptJson(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        if (accept != null) {
            List<MimeType> mimeTypes = MimeTypeUtils.parseMimeTypes(accept);
            for (MimeType mimeType : mimeTypes) {
                boolean include = MimeTypeUtils.APPLICATION_JSON.includes(mimeType);
                if (include) {
                    return true;
                }
            }
        }
        String uri = request.getRequestURI();
        if (StringUtils.endsWithIgnoreCase(uri,".json")) {
            return true;
        }
        return false;
    }

}
