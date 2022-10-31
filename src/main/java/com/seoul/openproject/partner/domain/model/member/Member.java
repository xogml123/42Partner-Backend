package com.seoul.openproject.partner.domain.model.member;


import com.seoul.openproject.partner.domain.MatchTryAvailabilityJudge;
import com.seoul.openproject.partner.domain.model.BaseTimeVersionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Pageable;


@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "MEMBER", uniqueConstraints = {
    @UniqueConstraint(name = "NICK_NAME_UNIQUE", columnNames = {"nickname"}),
    @UniqueConstraint(name = "API_ID_UNIQUE", columnNames = {"apiId"})
})
@Entity
public class Member extends BaseTimeVersionEntity {
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
    @Column(name = "MEMBER_ID")
    private Long id;


    /********************************* PK가 아닌 필드 *********************************/

    /**
     * AUTH에 필요한 필드
     */

    @Builder.Default
    @Column(nullable = false, updatable = false, length = 50)
    private final String apiId = UUID.randomUUID().toString();

    @Column(unique = true, nullable = false, length = 30)
    private String nickname;






    /********************************* 비영속 필드 *********************************/

    /********************************* 연관관계 매핑 *********************************/

    //FetchType.LAZY가 실질적으로 적용안됨 항상 EAGER로 적용
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "member", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private MatchTryAvailabilityJudge matchTryAvailabilityJudge;
    /********************************* 연관관계 편의 메서드 *********************************/

    /********************************* 생성 메서드 *********************************/
    public static Member of(String nickname, MatchTryAvailabilityJudge matchTryAvailabilityJudge) {
        Member member = Member.builder()
            .nickname(nickname)
            .build();
        matchTryAvailabilityJudge.setMember(member);
        return member;
    }

    /********************************* 비니지스 로직 *********************************/
    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }
    public void setMatchTryAvailabilityJudge(MatchTryAvailabilityJudge matchTryAvailabilityJudge) {
        this.matchTryAvailabilityJudge = matchTryAvailabilityJudge;
    }

    /********************************* DTO *********************************/

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class MemberDto {


        @Schema(name = "nickname" , example = "꿈꾸는 나무", description = "글 작성자 혹은 참여자 (member)의 nickname")
        @NotBlank
        private String nickname;

        @Schema(name = "isAuthor" , example = "true Or false", description = "작성자이면 true")
        @NotNull
        private Boolean isAuthor;

        @Schema(name = "anonymity" , example = "true Or false", description = "익명을 원하면 true")
        @NotNull
        private Boolean anonymity;

    }
}
