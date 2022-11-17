package partner42.modulecommon.repository.activity;


public interface ActivityRepositoryCustom {

    Integer findSumScoreByMemberIdAndArticleSearch(Long memberId, ActivitySearch activitySearch);
}
