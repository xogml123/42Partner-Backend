package partner42.moduleapi.service.activity;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import partner42.moduleapi.dto.activity.ActivityScoreResponse;
import partner42.modulecommon.domain.model.activity.ActivityMatchScore;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.user.User;
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

    public ActivityScoreResponse readMyActivityScoreSum(String username,
        ActivitySearch activitySearch) {
        Member member = getUserByUsernameOrException(username)
            .getMember();
        Integer scoreSum = activityRepository.findActivityMatchScoreByMemberIdAndArticleSearch(
                member.getId(),
                activitySearch)
            .stream().map(ActivityMatchScore::getScore)
            .reduce(0, Integer::sum);
        return ActivityScoreResponse.of(
            scoreSum);
    }

    private User getUserByUsernameOrException(String username) {
        return userRepository.findByUsername(username).orElseThrow(() ->
            new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
    }
}
