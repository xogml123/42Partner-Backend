package com.seoul.openproject.partner.config.bootloader;

import ch.qos.logback.core.net.server.Client;
import com.seoul.openproject.partner.domain.model.matchcondition.Place;
import com.seoul.openproject.partner.domain.model.matchcondition.TimeOfEating;
import com.seoul.openproject.partner.domain.model.matchcondition.TypeOfStudy;
import com.seoul.openproject.partner.domain.model.matchcondition.WayOfEating;
import com.seoul.openproject.partner.domain.model.match.ConditionCategory;
import com.seoul.openproject.partner.domain.model.matchcondition.MatchCondition;
import com.seoul.openproject.partner.domain.model.member.Member;
import com.seoul.openproject.partner.domain.model.tryjudge.MatchTryAvailabilityJudge;
import com.seoul.openproject.partner.domain.model.user.Authority;
import com.seoul.openproject.partner.domain.model.user.Role;
import com.seoul.openproject.partner.domain.model.user.RoleEnum;
import com.seoul.openproject.partner.domain.model.user.User;
import com.seoul.openproject.partner.domain.model.user.UserRole;
import com.seoul.openproject.partner.repository.matchcondition.MatchConditionRepository;
import com.seoul.openproject.partner.repository.member.MemberRepository;
import com.seoul.openproject.partner.repository.user.AuthorityRepository;
import com.seoul.openproject.partner.repository.user.RoleRepository;
import com.seoul.openproject.partner.repository.user.UserRepository;
import com.seoul.openproject.partner.repository.user.UserRoleRepository;
import com.seoul.openproject.partner.service.user.CustomOAuth2UserService;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistration.Builder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

//테스트 케이스 입력용
@RequiredArgsConstructor
@Component
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final MatchConditionRepository matchConditionRepository;

    private final CustomOAuth2UserService customOAuth2UserService;

    private final MemberRepository memberRepository;
    @Value("${spring.jpa.hibernate.data-loader}")
    private String dataLoader;
    @Transactional
    public void createRoleAuthority() {
//        Authority 생성
//        Authority createUser = saveNewAuthority("user.create");
//        Authority updateUser = saveNewAuthority("user.update");
//        Authority updateUserRole = saveNewAuthority("user.role.update");
//        Authority updateUserTeam = saveNewAuthority("user.team.update");
//        Authority updateUserAttendStatus = saveNewAuthority("user.attend-status.update");
//        Authority readUser = saveNewAuthority("user.read");
//        Authority deleteUser = saveNewAuthority("user.delete");
//
//
//        Authority createTodo = saveNewAuthority("todo.create");
//        Authority createUserTodo = saveNewAuthority("user.todo.create");
//        Authority updateTodo = saveNewAuthority("todo.update");
//        Authority updateUserTodo = saveNewAuthority("user.todo.update");
//        Authority readTodo = saveNewAuthority("todo.read");
//        Authority readUserTodo = saveNewAuthority("user.todo.read");
//        Authority deleteTodo = saveNewAuthority("todo.delete");
//        Authority deleteUserTodo = saveNewAuthority("user.todo.delete");
//
//
//        Authority createVacation = saveNewAuthority("vacation.create");
//        Authority createUserVacation = saveNewAuthority("user.vacation.create");
//        Authority updateVacation = saveNewAuthority("vacation.update");
//        Authority updateUserVacation = saveNewAuthority("user.vacation.update");
//        Authority readVacation = saveNewAuthority("vacation.read");
//        Authority readUserVacation = saveNewAuthority("user.vacation.read");
//        Authority deleteVacation = saveNewAuthority("vacation.delete");
//        Authority deleteUserVacation = saveNewAuthority("user.vacation.delete");
//
//
//
//        Authority createStudyTime = saveNewAuthority("study-time.create");
//        Authority updateStudyTime = saveNewAuthority("study-time.update");
//        Authority readStudyTime = saveNewAuthority("study-time.read");
//        Authority deleteStudyTime = saveNewAuthority("study-time.delete");
//
//        Authority createUserStudyTime = saveNewAuthority("user.study-time.create");
//        Authority updateUserStudyTime = saveNewAuthority("user.study-time.update");
//        Authority readUserStudyTime = saveNewAuthority("user.study-time.read");
//        Authority readStudyTimeUser = saveNewAuthority("study-time.user.read");
//        Authority deleteUserStudyTime = saveNewAuthority("user.study-time.delete");
//
//        Authority createAttendance = saveNewAuthority("attendance.create");
//        Authority updateAttendance = saveNewAuthority("attendance.update");
//        Authority readAttendance = saveNewAuthority("attendance.read");
//        Authority deleteAttendance = saveNewAuthority("attendance.delete");
//
//        Authority createUserAttendance = saveNewAuthority("user.attendance.create");
//        Authority updateUserAttendance = saveNewAuthority("user.attendance.update");
//        Authority readUserAttendance = saveNewAuthority("user.attendance.read");
//        Authority deleteUserAttendance = saveNewAuthority("user.attendance.delete");
//
//        Authority readDayLog = saveNewAuthority("day-log.read");
//
//        //있는 것 찾음
//        Authority createUser = authorityRepository.findByPermission("user.create").get();
//        Authority updateUser = authorityRepository.findByPermission("user.update").get();
//        Authority updateUserRole = authorityRepository.findByPermission("user.role.update").get();
//        Authority updateUserTeam = authorityRepository.findByPermission("user.team.update").get();
//        Authority updateUserAttendStatus = authorityRepository.findByPermission("user.attend-status.update").get();
//        Authority readUser = authorityRepository.findByPermission("user.read").get();
//        Authority deleteUser = authorityRepository.findByPermission("user.delete").get();
//
//
//        Authority createTodo = authorityRepository.findByPermission("todo.create").get();
//        Authority createUserTodo = authorityRepository.findByPermission("user.todo.create").get();
//        Authority updateTodo = authorityRepository.findByPermission("todo.update").get();
//        Authority updateUserTodo = authorityRepository.findByPermission("user.todo.update").get();
//        Authority readTodo = authorityRepository.findByPermission("todo.read").get();
//        Authority readUserTodo = authorityRepository.findByPermission("user.todo.read").get();
//        Authority deleteTodo = authorityRepository.findByPermission("todo.delete").get();
//        Authority deleteUserTodo = authorityRepository.findByPermission("user.todo.delete").get();
//
//
//        Authority createVacation = authorityRepository.findByPermission("vacation.create").get();
//        Authority createUserVacation = authorityRepository.findByPermission("user.vacation.create").get();
//        Authority updateVacation = authorityRepository.findByPermission("vacation.update").get();
//        Authority updateUserVacation = authorityRepository.findByPermission("user.vacation.update").get();
//        Authority readVacation = authorityRepository.findByPermission("vacation.read").get();
//        Authority readUserVacation = authorityRepository.findByPermission("user.vacation.read").get();
//        Authority deleteVacation = authorityRepository.findByPermission("vacation.delete").get();
//        Authority deleteUserVacation = authorityRepository.findByPermission("user.vacation.delete").get();
//
//
//
//        Authority createStudyTime = authorityRepository.findByPermission("study-time.create").get();
//        Authority updateStudyTime = authorityRepository.findByPermission("study-time.update").get();
//        Authority readStudyTime = authorityRepository.findByPermission("study-time.read").get();
//        Authority deleteStudyTime = authorityRepository.findByPermission("study-time.delete").get();
//
//        Authority createUserStudyTime = authorityRepository.findByPermission("user.study-time.create").get();
//        Authority updateUserStudyTime = authorityRepository.findByPermission("user.study-time.update").get();
//        Authority readUserStudyTime = authorityRepository.findByPermission("user.study-time.read").get();
//        Authority readStudyTimeUser = authorityRepository.findByPermission("study-time.user.read").get();
//        Authority deleteUserStudyTime = authorityRepository.findByPermission("user.study-time.delete").get();
//
//        Authority createAttendance = authorityRepository.findByPermission("attendance.create").get();
//        Authority updateAttendance = authorityRepository.findByPermission("attendance.update").get();
//        Authority readAttendance = authorityRepository.findByPermission("attendance.read").get();
//        Authority deleteAttendance = authorityRepository.findByPermission("attendance.delete").get();
//
//        Authority createUserAttendance = authorityRepository.findByPermission("user.attendance.create").get();
//        Authority updateUserAttendance = authorityRepository.findByPermission("user.attendance.update").get();
//        Authority readUserAttendance = authorityRepository.findByPermission("user.attendance.read").get();
//        Authority deleteUserAttendance = authorityRepository.findByPermission("user.attendance.delete").get();
//
//        Authority readDayLog = authorityRepository.findByPermission("day-log.read").get();
//

        //Authority createUser = saveNewAuthority("user.create");

        //Role 생성
        Role adminRole = saveNewRole(RoleEnum.ROLE_ADMIN);
        Role userRole = saveNewRole(RoleEnum.ROLE_USER);

//        Role adminRole = roleRepository.findByValue(RoleEnum.ROLE_ADMIN).get();
//        Role userRole = roleRepository.findByValue(RoleEnum.ROLE_USER).get();

//        adminRole.getAuthorities().clear();
//        adminRole.addAuthorities(createUser, updateUser,updateUserRole, updateUserTeam, updateUserAttendStatus,  readUser, deleteUser,
//            createTodo, createUserTodo, updateTodo, updateUserTodo, readTodo, readUserTodo, deleteTodo, deleteUserTodo,
//            createVacation, createUserVacation, updateVacation, updateUserVacation, readVacation, readUserVacation, deleteVacation, deleteUserVacation,
//            createStudyTime, updateStudyTime, readStudyTime, deleteStudyTime,
//            createUserStudyTime, updateUserStudyTime, readUserStudyTime, readStudyTimeUser, deleteUserStudyTime,
//            createAttendance, updateAttendance, readAttendance, deleteAttendance, createUserAttendance, updateUserAttendance, readUserAttendance, deleteUserAttendance,
//            readDayLog);
//
//        userRole.getAuthorities().clear();
//        userRole.addAuthorities(createUser, readUser, updateUser,
//            createUserTodo, updateUserTodo, readTodo, readUserTodo, deleteUserTodo,
//            createUserVacation, updateUserVacation,readUserVacation,
//            createUserStudyTime, updateUserStudyTime, readUserStudyTime, readStudyTimeUser, deleteUserStudyTime,
//                             updateAttendance, readAttendance, deleteAttendance, createUserAttendance, updateUserAttendance, readUserAttendance,
//            readDayLog);

        roleRepository.saveAll(Arrays.asList(adminRole, userRole));

    }

    @Transactional
    public void createMatchCondition(){
        matchConditionRepository.save(MatchCondition.of(WayOfEating.DELIVERY.name(), ConditionCategory.WayOfEating));
        matchConditionRepository.save(MatchCondition.of(WayOfEating.EATOUT.name(), ConditionCategory.WayOfEating));
        matchConditionRepository.save(MatchCondition.of(WayOfEating.TAKEOUT.name(), ConditionCategory.WayOfEating));

        matchConditionRepository.save(MatchCondition.of(Place.SEOCHO.name(), ConditionCategory.Place));
        matchConditionRepository.save(MatchCondition.of(Place.GAEPO.name(), ConditionCategory.Place));
        matchConditionRepository.save(MatchCondition.of(Place.OUT_OF_CLUSTER.name(), ConditionCategory.Place));

        matchConditionRepository.save(MatchCondition.of(TimeOfEating.BREAKFAST.name(), ConditionCategory.TimeOfEating));
        matchConditionRepository.save(MatchCondition.of(TimeOfEating.LUNCH.name(), ConditionCategory.TimeOfEating));
        matchConditionRepository.save(MatchCondition.of(TimeOfEating.DUNCH.name(), ConditionCategory.TimeOfEating));
        matchConditionRepository.save(MatchCondition.of(TimeOfEating.DINNER.name(), ConditionCategory.TimeOfEating));
        matchConditionRepository.save(MatchCondition.of(TimeOfEating.MIDNIGHT.name(), ConditionCategory.TimeOfEating));

//        matchConditionRepository.save(MatchCondition.of(TypeOfEating.KOREAN.name(), ConditionCategory.TypeOfEating));
//        matchConditionRepository.save(MatchCondition.of(TypeOfEating.JAPANESE.name(), ConditionCategory.TypeOfEating));
//        matchConditionRepository.save(MatchCondition.of(TypeOfEating.CHINESE.name(), ConditionCategory.TypeOfEating));
//        matchConditionRepository.save(MatchCondition.of(TypeOfEating.WESTERN.name(), ConditionCategory.TypeOfEating));
//        matchConditionRepository.save(MatchCondition.of(TypeOfEating.ASIAN.name(), ConditionCategory.TypeOfEating));
//        matchConditionRepository.save(MatchCondition.of(TypeOfEating.EXOTIC.name(), ConditionCategory.TypeOfEating));
//        matchConditionRepository.save(MatchCondition.of(TypeOfEating.CONVENIENCE.name(), ConditionCategory.TypeOfEating));

        matchConditionRepository.save(MatchCondition.of(TypeOfStudy.INNER_CIRCLE.name(), ConditionCategory.TypeOfStudy));
        matchConditionRepository.save(MatchCondition.of(TypeOfStudy.NOT_INNER_CIRCLE.name(), ConditionCategory.TypeOfStudy));


    }

    @Transactional
    public void createDefaultUsers(){

        Map<String, Object> sorkim = new HashMap<>();
        sorkim.put("id", 3);
        sorkim.put("login", "sorkim");
        sorkim.put("email", "sorkim@student.42seoul.kr");
        sorkim.put("image_url", "https://cdn.intra.42.fr/users/0f260cc3e59777f0f5ba926f19cc1ec9/sorkim.jpg");

        Map<String, Object> hyenam = new HashMap<>();
        hyenam.put("id", 4);
        hyenam.put("login", "hyenam");
        hyenam.put("email", "hyenam@student.42seoul.kr");
        hyenam.put("image_url", "https://cdn.intra.42.fr/users/0f260cc3e59777f0f5ba926f19cc1ec9/hyenam.jpg");

        Map<String, Object> admin = new HashMap<>();
        admin.put("id", 5);
        admin.put("login", "admin");
        admin.put("email", "admin@student.42seoul.kr");
        admin.put("image_url", "https://cdn.intra.42.fr/users/0f260cc3e59777f0f5ba926f19cc1ec9/admin.jpg");


        loadUser(sorkim);
        loadUser(hyenam);
        loadUser(admin);

    }

    private void loadUser(Map<String, Object> attributes) {
    String apiId = ((Integer)attributes.get("id")).toString();
    //takim
    String login = (String)attributes.get("login");
    //takim@student.42seoul.kr
    String email = (String) attributes.get("email");
    //https://cdn.intra.42.fr/users/0f260cc3e59777f0f5ba926f19cc1ec9/takim.jpg
    String imageUrl = (String) attributes.get("image_url");

    HashMap<String, Object> necessaryAttributes = createNecessaryAttributes(apiId, login,
        email, imageUrl);

    String username = email;
    Optional<User> userOptional = userRepository.findByUsername(username);
    User oAuth2User = signUpOrUpdateUser(login, email, imageUrl, username, userOptional, necessaryAttributes);
        oAuth2User.getAttributes().putAll(necessaryAttributes);
}

    private HashMap<String, Object> createNecessaryAttributes(String apiId, String login, String email, String imageUrl) {
        HashMap<String, Object> necessaryAttributes = new HashMap<>();
        necessaryAttributes.put("id", apiId);
        necessaryAttributes.put("login", login);
        necessaryAttributes.put("email", email);
        necessaryAttributes.put("image_url", imageUrl);
        return necessaryAttributes;
    }


    private User signUpOrUpdateUser(String login, String email, String imageUrl, String username,
        Optional<User> userOptional, Map<String, Object> necessaryAttributes) {
        User user;
        //회원가입
        if (userOptional.isEmpty()) {
            //회원에 필용한 정보 생성 및 조회

            String encodedPassword = passwordEncoder.encode(UUID.randomUUID().toString());

            MatchTryAvailabilityJudge matchTryAvailabilityJudge = MatchTryAvailabilityJudge.of();
            Member member = Member.of(login, matchTryAvailabilityJudge);
            memberRepository.save(member);

            Role role = roleRepository.findByValue(RoleEnum.ROLE_USER).orElseThrow(() ->
                new EntityNotFoundException(RoleEnum.ROLE_USER + "에 해당하는 Role이 없습니다."));
            user = User.of(username, encodedPassword, email, login, imageUrl, member);
            UserRole userRole = UserRole.of(role, user);

            userRepository.save(user);
            userRoleRepository.save(userRole);
            necessaryAttributes.put("create_flag", true);
            //생성해야할 객체 추가로 더 있을 수 있음.
        } else{
            //회원정보 수정
            user = userOptional.get();
            // 새로 로그인 시 oauth2 기반 데이터로 변경하지않음.
//            user.updateUserByOAuthIfo(imageUrl);
            necessaryAttributes.put("create_flag", false);
        }
        return user;
    }
    private Role saveNewRole(RoleEnum roleEnum) {
        Role role = Role.of(roleEnum);
        return roleRepository.save(role);
    }

    private Authority saveNewAuthority(String s) {
        return authorityRepository.save(
            Authority.of(s));
    }


    @Override
    public void run(String... args) throws Exception {
        if (dataLoader.equals("1")) {
            createRoleAuthority();
            createMatchCondition();
//            createDefaultUsers();
        }
    }
}
