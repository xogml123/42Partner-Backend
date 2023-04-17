package partner42.modulecommon.repository.activity;


import java.util.List;
import partner42.modulecommon.domain.model.activity.ActivityMatchScore;

public interface ActivityRepositoryCustom {

    List<ActivityMatchScore> findActivityMatchScoreByMemberIdAndArticleSearch(Long memberId, ActivitySearch activitySearch);
}
