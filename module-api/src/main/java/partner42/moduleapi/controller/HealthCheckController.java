package partner42.moduleapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping()
@RequiredArgsConstructor
public class HealthCheckController {

    /**
     * loadbalance health check  시 활용.
     * @param userId
     * @return
     */
    @GetMapping("/")
    public ResponseEntity<String> test(@PathVariable String userId) {
        return ResponseEntity.ok("test");
    }
}
