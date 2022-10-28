package com.seoul.openproject.partner.domain.model.match;

import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "STUDY_RANDOM_MATCH")
@Entity
public class StudyRandomMatching extends Matching {
    //********************************* static final 상수 필드 *********************************/

    /**
     * email 뒤에 붙는 문자열
     */

    /********************************* PK 필드 *********************************/

    /**
     * 기본 키
     */

}