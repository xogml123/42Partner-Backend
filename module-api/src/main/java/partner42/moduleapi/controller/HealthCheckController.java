package partner42.moduleapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class HealthCheckController {

    /**
     * loadbalance health check  시 활용.
     * @return
     */
    @GetMapping("/")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("test1237");
    }

//    @GetMapping("/health")
//    public Page<Object> test2() {
//        return new PageImpl<>(new ArrayList<>());
//    }
}
