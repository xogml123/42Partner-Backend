package partner42.moduleapi.service.activity;


import com.seoul.openproject.partner.repository.activity.ActivitySearch;
import com.seoul.openproject.partner.domain.model.activity.Activity.ActivityScoreResponse;
import com.seoul.openproject.partner.domain.model.member.Member;
import com.seoul.openproject.partner.error.exception.ErrorCode;
import com.seoul.openproject.partner.error.exception.NoEntityException;
import com.seoul.openproject.partner.repository.activity.ActivityRepository;
import com.seoul.openproject.partner.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    private final UserRepository userRepository;

    public ActivityScoreResponse readMyActivityScoreSum(String userId,
        ActivitySearch activitySearch) {
        Member member = userRepository.findByApiId(userId).orElseThrow(() ->
                new NoEntityException(ErrorCode.ENTITY_NOT_FOUND))
            .getMember();
        Integer scoreSum = activityRepository.findSumScoreByMemberIdAndArticleSearch(
            member.getId(),
            activitySearch);
        return ActivityScoreResponse.of(
            scoreSum == null ? 0: scoreSum) ;
    }
}
