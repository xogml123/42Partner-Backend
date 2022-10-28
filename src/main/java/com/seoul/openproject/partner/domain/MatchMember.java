package com.seoul.openproject.partner.domain;




import com.seoul.openproject.partner.domain.model.BaseTimeVersionEntity;
import com.seoul.openproject.partner.domain.model.match.Matching;
import com.seoul.openproject.partner.domain.model.member.Member;
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
public class MatchMember extends BaseTimeVersionEntity {
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

    /**
     * AUTH에 필요한 필드
     */






    /********************************* 비영속 필드 *********************************/

    /********************************* 연관관계 매핑 *********************************/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false, updatable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MATCH_ID", nullable = false, updatable = false)
    private Matching matching;



    /********************************* 연관관계 편의 메서드 *********************************/

    /********************************* 생성 메서드 *********************************/


    /********************************* 비니지스 로직 *********************************/

}
