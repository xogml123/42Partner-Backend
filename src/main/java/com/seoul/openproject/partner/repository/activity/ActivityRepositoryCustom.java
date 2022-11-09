package com.seoul.openproject.partner.repository.activity;


public interface ActivityRepositoryCustom {

    Integer findSumScoreByMemberIdAndArticleSearch(Long memberId, ActivitySearch activitySearch);
}
