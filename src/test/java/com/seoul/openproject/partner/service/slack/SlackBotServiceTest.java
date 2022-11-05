package com.seoul.openproject.partner.service.slack;

import com.seoul.openproject.partner.domain.model.user.User;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SlackBotServiceTest {

    @Autowired
    private SlackBotService slackBotService;
    @Test
    void getSlackIdByEmail() {
        Optional<String> userId1 = slackBotService.getSlackIdByEmail("xogml951" + User.GMAIL);
        Optional<String> userId2 = slackBotService.getSlackIdByEmail("sorkim" + User.SEOUL_42);

        ArrayList<String> userIds = new ArrayList<>();
        if (userId1.isPresent()){
            userIds.add(userId1.get());
        }
        if (userId2.isPresent()){
            userIds.add(userId2.get());
        }
        Optional<String> dmId = slackBotService.createMPIM(userIds);
        System.out.println("dmId = " + dmId);
        if (dmId.isPresent()){
            String 테스트_메시지 = slackBotService.sendMessage(dmId.get(), "테스트 메시지");
            System.out.println(테스트_메시지);
        }
    }
}