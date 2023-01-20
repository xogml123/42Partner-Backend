//package partner42.moduleapi.config.security;
//
//import java.io.IOException;
//import java.util.Collection;
//import java.util.logging.Filter;
//import java.util.logging.Logger;
//import javax.servlet.FilterChain;
//import javax.servlet.FilterConfig;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpHeaders;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//@Slf4j
//@Component
//public class SameSiteFilter extends OncePerRequestFilter {
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
//        FilterChain filterChain) throws ServletException, IOException {
//        filterChain.doFilter(request, response);
//
//        log.info("Same Site Filter Logging Response :{}", response.getContentType());
//
//        Collection<String> headers = response.getHeaders(HttpHeaders.SET_COOKIE);
//        boolean firstHeader = true;
//        for (String header : headers) { // there can be multiple Set-Cookie attributes
//            if (firstHeader) {
//                response.setHeader(HttpHeaders.SET_COOKIE,
//                    String.format("%s; %s", header, "SameSite=None"));
//                log.info(String.format("Same Site Filter First Header %s; %s", header,
//                    "SameSite=None; Secure"));
//
//                firstHeader = false;
//                continue;
//            }
//
//            response.addHeader(HttpHeaders.SET_COOKIE, String.format("%s; %s", header, "SameSite=None"));
//            log.info(String.format("Same Site Filter Remaining Headers %s; %s", header,
//                "SameSite=None; Secure"));
//        }
//
//    }
//
//
//}
