package partner42.modulecommon.domain.model.activity;


import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
//매치가 성공적으로 이루어졌을 때, 어떤 활동을 했는지를 나타내는 enum
public enum ActivityMatchScore {

    //상대방이 남긴 리뷰에 대한 점수
    MATCH_REVIEW_1("매치 리뷰 1점", -10),
    MATCH_REVIEW_2("매치 리뷰 2점", 0),
    MATCH_REVIEW_3("매치 리뷰 3점", 10),
    MATCH_REVIEW_4("매치 리뷰 4점", 20),
    MATCH_REVIEW_5("매치 리뷰 5점", 42),
    MATCH_ABSENT("매치 불참", -200),

    //매칭 참여 혹은 리뷰를 남겼을 때 점수를 부여한다.
    MATCH_PARTICIPANT("매치 참여자", 10),
    MATCH_REVIEW("매치 리뷰", 20),
    ARTICLE_MATCH_AUTHOR("방 매치 작성자", 42),
    ;

    private final String value;
    private final Integer score;

    @JsonCreator
    public static ActivityMatchScore from(String s) {
        String target = s.toUpperCase();
        return ActivityMatchScore.valueOf(target);
    }
}
