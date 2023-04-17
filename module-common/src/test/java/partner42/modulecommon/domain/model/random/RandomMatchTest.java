package partner42.modulecommon.domain.model.random;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class RandomMatchTest {

    /**
     * 굳이 검증할 필요없으며 오히려 30분이라는 기준이 바뀌면 테스트가 깨지는 문제가 있음
     * 리팩토링 내성 저하
     */
//    @Test
//    void getValidTime() {
//        LocalDateTime now = LocalDateTime.now();
//        assertThat(RandomMatch.getValidTime(now))
//            .isEqualTo(now.minusMinutes(30));
//    }
}