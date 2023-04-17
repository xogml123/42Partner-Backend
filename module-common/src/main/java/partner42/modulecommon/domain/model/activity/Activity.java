package partner42.modulecommon.domain.model.activity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import partner42.modulecommon.domain.model.BaseEntity;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.match.Match;
import partner42.modulecommon.domain.model.member.Member;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Getter
@Table(name = "ACTIVITY")
@Entity
public class Activity extends BaseEntity {
    //********************************* static final 상수 필드 *********************************/

    /********************************* PK 필드 *********************************/

    /**
     * 기본 키
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACTIVITY_ID")
    private Long id;

    /********************************* PK가 아닌 필드 *********************************/


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private ContentCategory contentCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private ActivityMatchScore activityMatchScore;


    /********************************* 비영속 필드 *********************************/

    /********************************* 연관관계 매핑 *********************************/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false, updatable = false)
    private Member member;

    /********************************* 연관관계 편의 메서드 *********************************/

    /********************************* 생성 메서드 *********************************/

    public static Activity of(Member member, ContentCategory contentCategory,
        ActivityMatchScore activityMatchScore) {
        return Activity.builder()
            .member(member)
            .contentCategory(contentCategory)
            .activityMatchScore(activityMatchScore)
            .build();
    }


    /********************************* 비니지스 로직 *********************************/


    /********************************* DTO *********************************/



}


