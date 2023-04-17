package partner42.moduleapi.controller.random;

import static org.mockito.BDDMockito.*;
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
import partner42.moduleapi.dto.random.MealRandomMatchDto;
import partner42.moduleapi.dto.random.RandomMatchDto;
import partner42.moduleapi.service.random.RandomMatchService;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.matchcondition.TypeOfStudy;
import partner42.modulecommon.domain.model.matchcondition.WayOfEating;
import partner42.modulecommon.domain.model.random.RandomMatch;
import partner42.modulecommon.domain.model.random.RandomMatchCondition;
import partner42.moduleapi.producer.random.RandomMatchProducer;

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
    @MockBean
    private RandomMatchProducer randomMatchProducer;


    @Test
    @WithMockUser
    void applyRandomMatch() throws Exception {

        RandomMatchDto randomMatchDto = MealRandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .placeList(List.of())
            .wayOfEatingList(List.of(WayOfEating.EATOUT))
            .build();

        //mock
        given(randomMatchService.createRandomMatch(any(), any(), any())).willReturn(List.of(RandomMatch.of(RandomMatchCondition.of(null, TypeOfStudy.INNER_CIRCLE), null)));
        //then
        mockMvc.perform(post("/api/random-matches")
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(randomMatchDto)))
            .andExpect(status().isCreated())
            .andDo(print());

    }
    @Test
    @WithMockUser
    void applyRandomMatch_whenRandomMatchDtoNotNullFieldWithNull_then400() throws Exception {
        RandomMatchDto randomMatchDto = MealRandomMatchDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .placeList(List.of())
            .build();
        //mock
        //then
        mockMvc.perform(post("/api/random-matches")
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(randomMatchDto)))
            .andExpect(status().isBadRequest())
            .andDo(print());

    }
}