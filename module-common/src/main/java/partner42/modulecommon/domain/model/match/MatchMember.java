package partner42.modulecommon.domain.model.match;




import partner42.modulecommon.domain.model.BaseEntity;
import partner42.modulecommon.domain.model.member.Member;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "MATCH_MEMBER")
@Entity
public class MatchMember extends BaseEntity {
    //********************************* static final 상수 필드 *********************************/

    /**
     * email 뒤에 붙는 문자열
     */


    /********************************* PK 필드 *********************************/

    /**
     * 기본 키
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MATCH_MEMBER_ID")
    private Long id;


    /********************************* PK가 아닌 필드 *********************************/


    @Column(name = "IS_AUTHOR", nullable = false, updatable = false)
    private Boolean isAuthor;





    /********************************* 비영속 필드 *********************************/

    /********************************* 연관관계 매핑 *********************************/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false, updatable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MATCH_ID", nullable = false, updatable = false)
    private Match match;



    /********************************* 연관관계 편의 메서드 *********************************/
    public void setMatch(Match match) {
        this.match = match;
        match.getMatchMembers().add(this);
    }

    /********************************* 생성 메서드 *********************************/

    public static MatchMember of(Match match, Member member, boolean isAuthor) {
        MatchMember matchMember = MatchMember.builder()
            .isAuthor(isAuthor)
            .member(member)
            .build();
        matchMember.setMatch(match);
        return matchMember;
    }
    /********************************* 비니지스 로직 *********************************/

}
