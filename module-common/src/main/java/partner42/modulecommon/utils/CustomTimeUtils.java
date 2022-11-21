package partner42.modulecommon.utils;

import java.time.LocalDateTime;

public class CustomTimeUtils {

    public static LocalDateTime nowWithoutNano() {
        return LocalDateTime.parse(LocalDateTime.now().toString().split("\\.")[0]);
    }
}
