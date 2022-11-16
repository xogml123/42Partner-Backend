package com.seoul.openproject.partner.service;

import static org.junit.jupiter.api.Assertions.*;

import com.seoul.openproject.partner.domain.model.match.ContentCategory;
import com.seoul.openproject.partner.domain.model.matchcondition.MatchCondition.MatchConditionRandomMatchDto;
import com.seoul.openproject.partner.domain.model.matchcondition.Place;
import com.seoul.openproject.partner.domain.model.random.RandomMatch;
import com.seoul.openproject.partner.domain.model.random.RandomMatch.RandomMatchCancelRequest;
import com.seoul.openproject.partner.domain.model.random.RandomMatch.RandomMatchDto;
import com.seoul.openproject.partner.repository.random.RandomMatchRedisRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RandomMatchServiceTest {

    @Autowired
    private RandomMatchService randomMatchService;
    @Autowired
    private RandomMatchRedisRepository randomMatchRedisRepository;

    @Test
    void createRandomMatch() {
        RandomMatchDto randomMatchDto = RandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto
                .builder()
                .placeList(List.of(Place.GAEPO))
                .build())
            .build();
        randomMatchService.createRandomMatch("dd8dba17-e655-4739-8b77-c874d94a11c6", randomMatchDto);

        RandomMatchDto randomMatchDto2 = RandomMatchDto.builder()
            .contentCategory(ContentCategory.STUDY)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto
                .builder()
                .placeList(List.of(Place.GAEPO))
                .build())
            .build();
        randomMatchService.createRandomMatch("dd8dba17-e655-4739-8b77-c874d94a11c6", randomMatchDto2);
    }

    @Test
    void deleteRandomMatch() {
        randomMatchService.deleteRandomMatch("dd8dba17-e655-4739-8b77-c874d94a11c6",
            RandomMatchCancelRequest.builder()
                .contentCategory(ContentCategory.MEAL).build());

        randomMatchService.deleteRandomMatch("dd8dba17-e655-4739-8b77-c874d94a11c6",
            RandomMatchCancelRequest.builder()
                .contentCategory(ContentCategory.STUDY).build());
//        randomMatchRedisRepository.deleteSortedSet();

    }
}