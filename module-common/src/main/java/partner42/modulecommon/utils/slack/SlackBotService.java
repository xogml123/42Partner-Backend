package partner42.modulecommon.utils.slack;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.exception.ErrorCode;
import partner42.modulecommon.exception.SlackException;

@Slf4j
@RequiredArgsConstructor
@Service
public class SlackBotService {

    public final SlackBotApi slackBotApi;

    /**
     * Async Thread pool 별도 사용
     * @param emails
     */
    @Async("threadPoolTaskExecutor")
    public void createSlackMIIM(List<String> emails) {
        List<String> slackIds = emails.stream()
            .map(email ->
                slackBotApi.getSlackIdByEmail(email))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());

        log.info("slackIds : {}", slackIds.toString());
        String MPIMId = slackBotApi.createMPIM(slackIds)
            .orElseThrow(() -> new SlackException(ErrorCode.SLACK_ERROR));
        slackBotApi.sendMessage(MPIMId, "매칭이 완료되었습니다!!\n"
            + "만약, 초대 되지않은 유저가 있다면 slack에서 초대해주세요.\n"
            + "slack email형식이 IntraId" + User.SEOUL_42
            + "와 맞지 않으면ㄴ 슬랙 초대 및 알림기능이 정상적으로 동작하지 않을 수 있습니다.");
    }


}
