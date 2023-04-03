package partner42.moduleapi.controller.random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import partner42.moduleapi.config.security.SecurityConfig;
import partner42.moduleapi.dto.matchcondition.MatchConditionRandomMatchDto;
import partner42.moduleapi.dto.random.RandomMatchDto;
import partner42.moduleapi.service.random.RandomMatchService;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.matchcondition.TypeOfStudy;
import partner42.modulecommon.domain.model.matchcondition.WayOfEating;

@WebMvcTest(value = {RandomMatchController.class},
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
    }
)
@AutoConfigureMockMvc(addFilters = false)
class RandomMatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RandomMatchService randomMatchService;

    @Test
    @WithMockUser
    void applyRandomMatch_whenMatchConditionRandomMatchDtoFieldExistNotCorrespondContentCategory_then400() throws Exception {
        RandomMatchDto randomMatchDto = RandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of())
                .typeOfStudyList(List.of(TypeOfStudy.INNER_CIRCLE))
                .wayOfEatingList(List.of())
                .build())
            .build();

        RandomMatchDto randomMatchDto1 = RandomMatchDto.builder()
            .contentCategory(ContentCategory.STUDY)
            .matchConditionRandomMatchDto(MatchConditionRandomMatchDto.builder()
                .placeList(List.of())
                .typeOfStudyList(List.of())
                .wayOfEatingList(List.of(WayOfEating.EATOUT))
                .build())
            .build();
        //then
        mockMvc.perform(post("/api/random-matches")
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(randomMatchDto)))
            .andExpect(status().isBadRequest())
            .andDo(print());

        mockMvc.perform(post("/api/random-matches")
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(randomMatchDto1)))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }
    @Test
    @WithMockUser
    void readRandomMatchCondition_whenContentCategoryNull_then400() throws Exception {
        mockMvc.perform(get("/api/random-matches/condition/mine"))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }
}