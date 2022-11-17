package partner42.moduleapi.service.activity;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import partner42.moduleapi.dto.activity.ActivityScoreResponse;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.exception.ErrorCode;
import partner42.modulecommon.exception.NoEntityException;
import partner42.modulecommon.repository.activity.ActivityRepository;
import partner42.modulecommon.repository.activity.ActivitySearch;
import partner42.modulecommon.repository.user.UserRepository;

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
