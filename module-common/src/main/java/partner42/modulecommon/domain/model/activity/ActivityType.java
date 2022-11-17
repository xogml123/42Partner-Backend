package partner42.modulecommon.domain.model.activity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ActivityType {
    RANDOM_MATCH("랜덤 매치", 10), RANDOM_MATCH_REVIEW("랜덤 매치 리뷰", 10),
    RANDOM_MATCH_REVIEW_1("랜덤 매치 리뷰 1점", -10),
    RANDOM_MATCH_REVIEW_2("랜덤 매치 리뷰 2점", 0),
    RANDOM_MATCH_REVIEW_3("랜덤 매치 리뷰 3점", 10),
    RANDOM_MATCH_REVIEW_4("랜덤 매치 리뷰 4점", 20),
    RANDOM_MATCH_REVIEW_5("랜덤 매치 리뷰 5점", 42),
    RANDOM_MATCH_ABSENT("랜덤 매치 불참", -100),

    ARTICLE_AUTHOR_MATCH("방 매치 작성자" , 42),
    ARTICLE_PARTICIPANT_MATCH("방 매치 참여자", 10),
    ARTICLE_MATCH_REVIEW("방 매치 리뷰", 10),
    ARTICLE_MATCH_REVIEW_1("방 매치 리뷰 1점", -10),
    ARTICLE_MATCH_REVIEW_2("방 매치 리뷰 2점", 0),
    ARTICLE_MATCH_REVIEW_3("방 매치 리뷰 3점", 10),
    ARTICLE_MATCH_REVIEW_4("방 매치 리뷰 4점", 20),
    ARTICLE_MATCH_REVIEW_5("방 매치 리뷰 5점", 42),
    ARTICLE_MATCH_ABSENT("방 매치 불참", -100);

    private final String value;
    private final Integer score;

}
