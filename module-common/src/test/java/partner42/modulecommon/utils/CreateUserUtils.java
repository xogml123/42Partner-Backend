package partner42.modulecommon.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.tryjudge.MatchTryAvailabilityJudge;
import partner42.modulecommon.domain.model.user.Role;
import partner42.modulecommon.domain.model.user.RoleEnum;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.domain.model.user.UserRole;
import partner42.modulecommon.repository.member.MemberRepository;
import partner42.modulecommon.repository.user.RoleRepository;
import partner42.modulecommon.repository.user.UserRepository;
import partner42.modulecommon.repository.user.UserRoleRepository;

@Component
public class CreateUserUtils {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    public void setUp() {
        Map<String, Object> sorkim = new HashMap<>();
        sorkim.put("id", 3);
        sorkim.put("login", "sorkim");
        sorkim.put("email", "sorkim@student.42seoul.kr");
        sorkim.put("image_url",
            "https://cdn.intra.42.fr/users/0f260cc3e59777f0f5ba926f19cc1ec9/sorkim.jpg");

        Map<String, Object> hyenam = new HashMap<>();
        hyenam.put("id", 4);
        hyenam.put("login", "hyenam");
        hyenam.put("email", "hyenam@student.42seoul.kr");
        hyenam.put("image_url",
            "https://cdn.intra.42.fr/users/0f260cc3e59777f0f5ba926f19cc1ec9/hyenam.jpg");

        Map<String, Object> takim = new HashMap<>();
        takim.put("id", 4);
        takim.put("login", "takim");
        takim.put("email", "takim@student.42seoul.kr");
        takim.put("image_url",
            "https://cdn.intra.42.fr/users/0f260cc3e59777f0f5ba926f19cc1ec9/takim.jpg");

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

        HashMap<String, Object> necessaryAttributes = createNecessaryAttributes(apiId, login,
            email, imageUrl);

        String username = email;
        Optional<User> userOptional = userRepository.findByUsername(username);
        User oAuth2User = signUpOrUpdateUser(login, email, imageUrl, username, userOptional,
            necessaryAttributes);
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


    private User signUpOrUpdateUser(String login, String email, String imageUrl, String username,
        Optional<User> userOptional, Map<String, Object> necessaryAttributes) {
        User user;
        //회원가입
        if (userOptional.isEmpty()) {
            //회원에 필용한 정보 생성 및 조회


            MatchTryAvailabilityJudge matchTryAvailabilityJudge = MatchTryAvailabilityJudge.of();
            Member member = Member.of(login, matchTryAvailabilityJudge);
            memberRepository.save(member);

            Role role = roleRepository.findByValue(RoleEnum.ROLE_USER).orElseThrow(() ->
                new EntityNotFoundException(RoleEnum.ROLE_USER + "에 해당하는 Role이 없습니다."));
            user = User.of(username, UUID.randomUUID().toString(), email, login, imageUrl, member);
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
