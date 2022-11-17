package partner42.moduleapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import partner42.modulecommon.service.TestService;

@RequiredArgsConstructor
@RestController
public class Controller {

    private final TestService testService;
}
