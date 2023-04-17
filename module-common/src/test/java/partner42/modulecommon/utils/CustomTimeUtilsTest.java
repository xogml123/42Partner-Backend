package partner42.modulecommon.utils;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CustomTimeUtilsTest {

    @Test
    void nowWithoutNano() {
        LocalDateTime localDateTime = CustomTimeUtils.nowWithoutNano();
        assertThat(localDateTime.getNano()).isEqualTo(0);
    }
}