package partner42.modulecommon.repository.alarm;


import static org.assertj.core.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import partner42.modulecommon.domain.model.alarm.Alarm;
import partner42.modulecommon.domain.model.alarm.AlarmArgs;
import partner42.modulecommon.domain.model.alarm.AlarmType;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.repository.member.MemberRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AlarmRepositoryCustomImplTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    AlarmRepository alarmRepository;

    @BeforeEach
    void setUp() {



    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findSliceByMemberId_givenDifferentAlarmWithDifferentMember_whenFindByMemberId_thenContainsExactly() {
        //given
        Member memberNotFind = Member.of("memberNotFind");
        Member memberFind = Member.of("memberFind");
        memberRepository.saveAll(List.of(memberNotFind, memberFind));

        Alarm alarmMemberFind1 = Alarm.of(AlarmType.COMMENT_ON_MY_COMMENT, new AlarmArgs(), memberFind);
        Alarm alarmMemberFind2 = Alarm.of(AlarmType.COMMENT_ON_MY_COMMENT, new AlarmArgs(), memberFind);
        Alarm alarmMemberFind3 = Alarm.of(AlarmType.COMMENT_ON_MY_COMMENT, new AlarmArgs(), memberFind);

        Alarm alarmMemberNotFind = Alarm.of(AlarmType.COMMENT_ON_MY_COMMENT, new AlarmArgs(), memberNotFind);

        alarmRepository.saveAll(List.of(alarmMemberFind1, alarmMemberFind2, alarmMemberFind3, alarmMemberNotFind));
        PageRequest page = PageRequest.of(0, 3, Sort.by(Order.desc("createdAt")));

        //when
        Slice<Alarm> alarmSlices = alarmRepository.findSliceByMemberId(page,
            memberFind.getId());
        //then
        assertThat(alarmSlices.getContent())
            .containsExactly(alarmMemberFind3, alarmMemberFind2, alarmMemberFind1);

    }

    @Test
    void findSliceByMemberId_givenAlarms_whenMemberIdNull_thenThrowsException() {
        //given
        Member memberNotFind = Member.of("memberNotFind");
        Member memberFind = Member.of("memberFind");
        memberRepository.saveAll(List.of(memberNotFind, memberFind));

        Alarm alarmMemberFind1 = Alarm.of(AlarmType.COMMENT_ON_MY_COMMENT, new AlarmArgs(), memberFind);
        Alarm alarmMemberFind2 = Alarm.of(AlarmType.COMMENT_ON_MY_COMMENT, new AlarmArgs(), memberFind);
        Alarm alarmMemberFind3 = Alarm.of(AlarmType.COMMENT_ON_MY_COMMENT, new AlarmArgs(), memberFind);

        Alarm alarmMemberNotFind = Alarm.of(AlarmType.COMMENT_ON_MY_COMMENT, new AlarmArgs(), memberNotFind);

        alarmRepository.saveAll(List.of(alarmMemberFind1, alarmMemberFind2, alarmMemberFind3, alarmMemberNotFind));

        PageRequest page = PageRequest.of(0, 3, Sort.by(Order.desc("createdAt")));
        Long findMemberId = null;
        //when

        //then
        assertThatThrownBy(() -> alarmRepository.findSliceByMemberId(page,
            findMemberId)).isInstanceOf(Exception.class);
    }

    @Test
    void findSliceByMemberId_givenAlarms_whenPagingSizeIsDiverse_thenNextFlag() {
        //given
        Member memberNotFind = Member.of("memberNotFind");
        Member memberFind = Member.of("memberFind");
        memberRepository.saveAll(List.of(memberNotFind, memberFind));

        Alarm alarmMemberFind1 = Alarm.of(AlarmType.COMMENT_ON_MY_COMMENT, new AlarmArgs(), memberFind);
        Alarm alarmMemberFind2 = Alarm.of(AlarmType.COMMENT_ON_MY_COMMENT, new AlarmArgs(), memberFind);
        Alarm alarmMemberFind3 = Alarm.of(AlarmType.COMMENT_ON_MY_COMMENT, new AlarmArgs(), memberFind);

        Alarm alarmMemberNotFind = Alarm.of(AlarmType.COMMENT_ON_MY_COMMENT, new AlarmArgs(), memberNotFind);

        alarmRepository.saveAll(List.of(alarmMemberFind1, alarmMemberFind2, alarmMemberFind3, alarmMemberNotFind));

        PageRequest pageEqual = PageRequest.of(0, 3, Sort.by(Order.desc("createdAt")));
        PageRequest pageLarger = PageRequest.of(0, 4, Sort.by(Order.desc("createdAt")));
        PageRequest pageLower = PageRequest.of(0, 2, Sort.by(Order.desc("createdAt")));


        Long findMemberId = memberFind.getId();
        //when
        Slice<Alarm> slicePageSizeEqual= alarmRepository.findSliceByMemberId(pageEqual,
            findMemberId);

        Slice<Alarm> slicePareSizeLarger = alarmRepository.findSliceByMemberId(pageLarger,
            findMemberId);
        Slice<Alarm> slicePageSizeLower = alarmRepository.findSliceByMemberId(pageLower,
            findMemberId);
        //then
        assertThat(slicePageSizeEqual.hasNext()).isFalse();
        assertThat(slicePareSizeLarger.hasNext()).isFalse();
        assertThat(slicePageSizeLower.hasNext()).isTrue();

    }
}