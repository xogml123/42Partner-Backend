package com.seoul.openproject.partner.config.bootloader;

import com.seoul.openproject.partner.domain.model.matchcondition.Place;
import com.seoul.openproject.partner.domain.model.matchcondition.TimeOfEating;
import com.seoul.openproject.partner.domain.model.matchcondition.TypeOfStudy;
import com.seoul.openproject.partner.domain.model.matchcondition.WayOfEating;
import com.seoul.openproject.partner.domain.model.match.ConditionCategory;
import com.seoul.openproject.partner.domain.model.matchcondition.MatchCondition;
import com.seoul.openproject.partner.domain.model.user.Authority;
import com.seoul.openproject.partner.domain.model.user.Role;
import com.seoul.openproject.partner.domain.model.user.RoleEnum;
import com.seoul.openproject.partner.repository.matchcondition.MatchConditionRepository;
import com.seoul.openproject.partner.repository.user.AuthorityRepository;
import com.seoul.openproject.partner.repository.user.RoleRepository;
import com.seoul.openproject.partner.repository.user.UserRepository;
import com.seoul.openproject.partner.repository.user.UserRoleRepository;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @Value("${spring.jpa.hibernate.data-loader}")
    private String dataLoader;
    private
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
        }
    }
}
