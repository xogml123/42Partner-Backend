package com.seoul.openproject.partner.repository.activity;

import com.seoul.openproject.partner.domain.model.match.ContentCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Setter
public class ActivitySearch {

    @Schema(hidden = true)
    @Value("${date.opening}")
    private LocalDateTime openingDate;

    @Schema(name = "contentCategory", example = "MEAL or STUDY", description = "식사, 공부 글인지 여부, 값을 꼭 지정해야함.")
    @NotNull
    private ContentCategory contentCategory;

    @Schema(name = "year", example = "2022-11-11 00:00:00", description = "점수 계산 시작 시간, 이 시간 부터 포함됨.따라서 2022-11-11 부터 원할 경우 2022-11-11 00:00:00라고 요청. 안쓰면 2022-12-01 00:00:00")
    private LocalDateTime startTime = openingDate;

    @Schema(name = "year", example = "2022-11-11 00:00:00", description = "점수 계산 종료 시간, 이 시간바로 직전까지 포함됨. 따라서 2022-11-11 까지 원할 경우 2022-11-12 00:00:00라고 요청. 안쓰면 현재시간")
    private LocalDateTime endTime = LocalDateTime.now();
}
