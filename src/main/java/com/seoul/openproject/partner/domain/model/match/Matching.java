package com.seoul.openproject.partner.domain.model.match;


import com.seoul.openproject.partner.domain.model.BaseTimeVersionEntity;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Getter;


@Getter
@Table(name = "MATCHING", uniqueConstraints = {
    @UniqueConstraint(name = "API_ID_UNIQUE", columnNames = {"apiId"}),
})
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "MATCH_TYPE")
@Entity
public abstract class Matching extends BaseTimeVersionEntity {
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
    @Column(name = "MATCH_ID")
    private Long id;


    /********************************* PK가 아닌 필드 *********************************/

    /**
     * AUTH에 필요한 필드
     */

//    @Builder.Default
    @Column(nullable = false, updatable = false, length = 50)
    private final String apiId = UUID.randomUUID().toString();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private MatchStatus matchStatus;







    /********************************* 비영속 필드 *********************************/

    /********************************* 연관관계 매핑 *********************************/




    /********************************* 연관관계 편의 메서드 *********************************/

    /********************************* 생성 메서드 *********************************/




    /********************************* 비니지스 로직 *********************************/

}

