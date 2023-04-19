package partner42.modulecommon.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import partner42.modulecommon.domain.model.match.ConditionCategory;
import partner42.modulecommon.domain.model.matchcondition.MatchCondition;
import partner42.modulecommon.domain.model.matchcondition.Place;
import partner42.modulecommon.domain.model.matchcondition.TimeOfEating;
import partner42.modulecommon.domain.model.matchcondition.TypeOfStudy;
import partner42.modulecommon.domain.model.matchcondition.WayOfEating;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.user.Authority;
import partner42.modulecommon.domain.model.user.Role;
import partner42.modulecommon.domain.model.user.RoleEnum;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.domain.model.user.UserRole;
import partner42.modulecommon.repository.matchcondition.MatchConditionRepository;
import partner42.modulecommon.repository.member.MemberRepository;
import partner42.modulecommon.repository.user.AuthorityRepository;
import partner42.modulecommon.repository.user.RoleRepository;
import partner42.modulecommon.repository.user.UserRepository;
import partner42.modulecommon.repository.user.UserRoleRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class BootstrapDataLoader {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final UserRoleRepository userRoleRepository;
    private final AuthorityRepository authorityRepository;

    private final RoleRepository roleRepository;

    private final MatchConditionRepository matchConditionRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void createDefaultUsers() {
        createRoleAuthority();

        Map<String, Object> sorkim = new HashMap<>();
        sorkim.put("id", 3);
        sorkim.put("login", "sorkim");
        sorkim.put("email", "sorkim@student.42seoul.kr");
        sorkim.put("image_url",
            "https://cdn.intra.42.fr/users/0f260cc3e59777f0f5ba926f19cc1ec9/sorkim.jpg");
        sorkim.put("role", RoleEnum.ROLE_ADMIN);

        Map<String, Object> hyenam = new HashMap<>();
        hyenam.put("id", 4);
        hyenam.put("login", "hyenam");
        hyenam.put("email", "hyenam@student.42seoul.kr");
        hyenam.put("image_url",
            "https://cdn.intra.42.fr/users/0f260cc3e59777f0f5ba926f19cc1ec9/hyenam.jpg");
        hyenam.put("role", RoleEnum.ROLE_ADMIN);

        Map<String, Object> takim = new HashMap<>();
        takim.put("id", 5);
        takim.put("login", "takim");
        takim.put("email", "takim@student.42seoul.kr");
        takim.put("image_url",
            "https://cdn.intra.42.fr/users/0f260cc3e59777f0f5ba926f19cc1ec9/takim.jpg");
        takim.put("role", RoleEnum.ROLE_ADMIN);

        loadUser(sorkim);
        loadUser(hyenam);
        loadUser(takim);
    }

    private void loadUser(Map<String, Object> attributes) {
        String apiId = ((Integer) attributes.get("id")).toString();
        //takim
        String login = (String) attributes.get("login");
        //takim@student.42seoul.kr
        String email = (String) attributes.get("email");
        //https://cdn.intra.42.fr/users/0f260cc3e59777f0f5ba926f19cc1ec9/takim.jpg
        String imageUrl = (String) attributes.get("image_url");

        RoleEnum role = (RoleEnum) attributes.get("role");
        HashMap<String, Object> necessaryAttributes = createNecessaryAttributes(apiId, login,
            email, imageUrl);

        String username = login;
        Optional<User> userOptional = userRepository.findByUsername(username);
        User oAuth2User = signUpOrUpdateUser(login, email, imageUrl, username, userOptional,
            necessaryAttributes, role);
    }

    private HashMap<String, Object> createNecessaryAttributes(String apiId, String login,
        String email, String imageUrl) {
        HashMap<String, Object> necessaryAttributes = new HashMap<>();
        necessaryAttributes.put("id", apiId);
        necessaryAttributes.put("login", login);
        necessaryAttributes.put("email", email);
        necessaryAttributes.put("image_url", imageUrl);
        return necessaryAttributes;
    }


    private void createRoleAuthority() {
        //Authority 생성
        /**
         * 권한 규칙은 다음과 같다.
         * 1. 마지막에는 create, update, read, delete가 붙는다.
         * 2. 앞은 api의 도메인을 의미한다.
         * 3. 예를 들어 opinion.delete는 모든 opinion을 제거할 수 있다는 의미이다.
         * 4. 만약, 자기 opinion만 삭제할 수 있다는 내용이 있다면 service에서 처리한다.
         */

        Authority createUser = saveNewAuthority("user.create");
        Authority updateUser = saveNewAuthority("user.update");
        Authority readUser = saveNewAuthority("user.read");
        Authority deleteUser = saveNewAuthority("user.delete");

        log.info("--------------------------------------------------------");
        Authority createOpinion = saveNewAuthority("opinion.create");
        Authority updateOpinion = saveNewAuthority("opinion.update");
        Authority readOpinion = saveNewAuthority("opinion.read");
        Authority deleteOpinion = saveNewAuthority("opinion.delete");
        log.info("--------------------------------------------------------");

        Authority createArticle = saveNewAuthority("article.create");
        Authority updateArticle = saveNewAuthority("article.update");
        Authority readArticle = saveNewAuthority("article.read");
        Authority deleteArticle = saveNewAuthority("article.delete");
        log.info("--------------------------------------------------------");

        Authority createMatch = saveNewAuthority("match.create");
        Authority updateMatch = saveNewAuthority("match.update");
        Authority readMatch = saveNewAuthority("match.read");
        Authority deleteMatch = saveNewAuthority("match.delete");
        log.info("--------------------------------------------------------");

        Authority createActivity = saveNewAuthority("activity.create");
        Authority updateActivity = saveNewAuthority("activity.update");
        Authority readActivity = saveNewAuthority("activity.read");
        Authority deleteActivity = saveNewAuthority("activity.delete");

        Authority createRandomMatch = saveNewAuthority("random-match.create");
        Authority updateRandomMatch = saveNewAuthority("random-match.update");
        Authority readRandomMatch = saveNewAuthority("random-match.read");
        Authority deleteRandomMatch = saveNewAuthority("random-match.delete");

        Authority createAlarm = saveNewAuthority("alarm.create");
        Authority updateAlarm = saveNewAuthority("alarm.update");
        Authority readAlarm = saveNewAuthority("alarm.read");
        Authority deleteAlarm = saveNewAuthority("alarm.delete");

        Authority readActuator = saveNewAuthority("actuator.read");

        //Role 생성
        Role adminRole = saveNewRole(RoleEnum.ROLE_ADMIN);
        Role userRole = saveNewRole(RoleEnum.ROLE_USER);

//        Role adminRole = roleRepository.findByValue(RoleEnum.ROLE_ADMIN).get();
//        Role userRole = roleRepository.findByValue(RoleEnum.ROLE_USER).get();

        adminRole.getAuthorities().clear();
        adminRole.addAuthorities(createUser, updateUser, readUser, deleteUser,
            createOpinion, updateOpinion, readOpinion, deleteOpinion,
            createArticle, updateArticle, readArticle, deleteArticle,
            createMatch, updateMatch, readMatch, deleteMatch,
            createActivity, updateActivity, readActivity, deleteActivity,
            createRandomMatch, updateRandomMatch, readRandomMatch, deleteRandomMatch,
            createAlarm, updateAlarm, readAlarm, deleteAlarm,
            readActuator);

        userRole.getAuthorities().clear();
        userRole.addAuthorities(createUser, updateUser, readUser, deleteUser,
            createOpinion, updateOpinion, readOpinion, deleteOpinion,
            createArticle, updateArticle, readArticle, deleteArticle,
            createMatch, updateMatch, readMatch, deleteMatch,
            createActivity, updateActivity, readActivity, deleteActivity,
            createRandomMatch, updateRandomMatch, readRandomMatch, deleteRandomMatch,
            createAlarm, updateAlarm, readAlarm, deleteAlarm);

        roleRepository.saveAll(Arrays.asList(adminRole, userRole));
    }

    public void createMatchCondition(){
        matchConditionRepository.save(
            MatchCondition.of(WayOfEating.DELIVERY.name(), ConditionCategory.WayOfEating));
        matchConditionRepository.save(
            MatchCondition.of(WayOfEating.EATOUT.name(), ConditionCategory.WayOfEating));
        matchConditionRepository.save(
            MatchCondition.of(WayOfEating.TAKEOUT.name(), ConditionCategory.WayOfEating));

        matchConditionRepository.save(
            MatchCondition.of(Place.SEOCHO.name(), ConditionCategory.Place));
        matchConditionRepository.save(
            MatchCondition.of(Place.GAEPO.name(), ConditionCategory.Place));
        matchConditionRepository.save(
            MatchCondition.of(Place.OUT_OF_CLUSTER.name(), ConditionCategory.Place));

        matchConditionRepository.save(
            MatchCondition.of(TimeOfEating.BREAKFAST.name(), ConditionCategory.TimeOfEating));
        matchConditionRepository.save(
            MatchCondition.of(TimeOfEating.LUNCH.name(), ConditionCategory.TimeOfEating));
        matchConditionRepository.save(
            MatchCondition.of(TimeOfEating.DUNCH.name(), ConditionCategory.TimeOfEating));
        matchConditionRepository.save(
            MatchCondition.of(TimeOfEating.DINNER.name(), ConditionCategory.TimeOfEating));
        matchConditionRepository.save(
            MatchCondition.of(TimeOfEating.MIDNIGHT.name(), ConditionCategory.TimeOfEating));

//        matchConditionRepository.save(MatchCondition.of(TypeOfEating.KOREAN.name(), ConditionCategory.TypeOfEating));
//        matchConditionRepository.save(MatchCondition.of(TypeOfEating.JAPANESE.name(), ConditionCategory.TypeOfEating));
//        matchConditionRepository.save(MatchCondition.of(TypeOfEating.CHINESE.name(), ConditionCategory.TypeOfEating));
//        matchConditionRepository.save(MatchCondition.of(TypeOfEating.WESTERN.name(), ConditionCategory.TypeOfEating));
//        matchConditionRepository.save(MatchCondition.of(TypeOfEating.ASIAN.name(), ConditionCategory.TypeOfEating));
//        matchConditionRepository.save(MatchCondition.of(TypeOfEating.EXOTIC.name(), ConditionCategory.TypeOfEating));
//        matchConditionRepository.save(MatchCondition.of(TypeOfEating.CONVENIENCE.name(), ConditionCategory.TypeOfEating));

        matchConditionRepository.save(
            MatchCondition.of(TypeOfStudy.INNER_CIRCLE.name(), ConditionCategory.TypeOfStudy));
        matchConditionRepository.save(
            MatchCondition.of(TypeOfStudy.NOT_INNER_CIRCLE.name(), ConditionCategory.TypeOfStudy));

    }

    private Role saveNewRole(RoleEnum roleEnum) {
        Role role = Role.of(roleEnum);
        return roleRepository.save(role);
    }

    private Authority saveNewAuthority(String s) {
        return authorityRepository.save(
            Authority.of(s));
    }

    private User signUpOrUpdateUser(String login, String email, String imageUrl, String username,
        Optional<User> userOptional, Map<String, Object> necessaryAttributes, RoleEnum roleEnum) {
        User user;
        //회원가입
        if (userOptional.isEmpty()) {
            //회원에 필용한 정보 생성 및 조회
            Member member = Member.of(login);
            memberRepository.save(member);
            Role role = roleRepository.findByValue(roleEnum).orElseThrow(() ->
                new EntityNotFoundException(roleEnum + "에 해당하는 Role이 없습니다."));
            user = User.of(username, bCryptPasswordEncoder.encode(username), email, login, imageUrl, member);
            UserRole userRole = UserRole.of(role, user);

            userRepository.save(user);
            userRoleRepository.save(userRole);
            necessaryAttributes.put("create_flag", true);
            //생성해야할 객체 추가로 더 있을 수 있음.
        } else {
            //회원정보 수정
            user = userOptional.get();
            // 새로 로그인 시 oauth2 기반 데이터로 변경하지않음.
//            user.updateUserByOAuthIfo(imageUrl);
            necessaryAttributes.put("create_flag", false);
        }
        return user;
    }
}
