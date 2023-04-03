package partner42.moduleapi.service.user;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import partner42.moduleapi.dto.user.CustomAuthenticationPrincipal;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.user.Role;
import partner42.modulecommon.domain.model.user.RoleEnum;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.domain.model.user.UserRole;
import partner42.modulecommon.repository.member.MemberRepository;
import partner42.modulecommon.repository.user.RoleRepository;
import partner42.modulecommon.repository.user.UserRepository;
import partner42.modulecommon.repository.user.UserRoleRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @RequiredArgsConstructor
    @Configuration
    static public class CustomOAuth2UserServiceWithDaoTestConfig {
        private final UserRepository userRepository;
        private final RoleRepository roleRepository;
        private final UserRoleRepository userRoleRepository;
        private final MemberRepository memberRepository;
        private final BCryptPasswordEncoder passwordEncoder;
        @Bean
        @Qualifier("customOAuth2UserService")
        public CustomOAuth2UserService customOAuth2UserService(
            DefaultOAuth2UserService defaultOAuth2UserService) {
            return new CustomOAuth2UserService(userRepository,
                roleRepository, userRoleRepository, memberRepository, passwordEncoder, defaultOAuth2UserService());
        }

        @Bean
        @Qualifier("defaultOAuth2UserService")
        public DefaultOAuth2UserService defaultOAuth2UserService() {
            return new DefaultOAuth2UserService();
        }
    }
    private static final String ID_ATTRIBUTE = "id";
    private static final String LOGIN_ATTRIBUTE = "login";
    private static final String EMAIL_ATTRIBUTE = "email";
    private static final String IMAGE_ATTRIBUTE = "image";
    private static final String LINK_ATTRIBUTE = "link";
    private static final String CREATE_FLAG = "create_flag";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * ClientRegistration을 가지고 실제 Resource Server로 부터 유저 정보를 받아오는 메소드이기 때문에
     * Mocking을 해야하는데 defaultOAuth2UserService를 주입받아서 사용하지 않으면
     * 단위 테스트 시 Mocking을 할 수 없음.
     */
    @Qualifier("defaultOAuth2UserService")
    private final DefaultOAuth2UserService defaultOAuth2UserService;

    /**
     * OAuth2 Code Grant 방식으로 인증을 진행하고, 인증이 완료되고 나서 Resource Server로 부터
     * 유저 정보를 받아오면 OAuth2UserRequest에 담겨 있음.
     * 해당 유저 정보가 DB에 없으면 회원가입을 진행하고 있으면 로그인을 진행.
     * @param userRequest the user request
     * @return
     * @throws OAuth2AuthenticationException
     */
    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        Map<String, Object> attributes = defaultOAuth2UserService.loadUser(userRequest).getAttributes();
        //resource Server로 부터 받아온 정보중 필요한 정보 추출.
        String apiId = ((Integer)attributes.get(ID_ATTRIBUTE)).toString();
        //takim
        String login = (String)attributes.get(LOGIN_ATTRIBUTE);
        //takim@student.42seoul.kr
        String email = (String) attributes.get(EMAIL_ATTRIBUTE);

        String imageUrl = "";
        //https://cdn.intra.42.fr/users/0f26777f0f5ba926f19cc1ec9/takim.jpg
        if (attributes.get(IMAGE_ATTRIBUTE) instanceof Map) {
            imageUrl = (String)((Map)(attributes.get(IMAGE_ATTRIBUTE))).get(LINK_ATTRIBUTE) == null ?
                "" : (String)((Map)(attributes.get(IMAGE_ATTRIBUTE))).get(LINK_ATTRIBUTE);
        }
        HashMap<String, Object> necessaryAttributes = createNecessaryAttributes(apiId, login,
            email, imageUrl);

        String username = email;
        Optional<User> userOptional = userRepository.findByUsername(username);
        OAuth2User oAuth2User = signUpOrUpdateUser(login, email, imageUrl, username, userOptional, necessaryAttributes);
        return oAuth2User;
    }

    private HashMap<String, Object> createNecessaryAttributes(String apiId, String login, String email, String imageUrl) {
        HashMap<String, Object> necessaryAttributes = new HashMap<>();
        necessaryAttributes.put(ID_ATTRIBUTE, apiId);
        necessaryAttributes.put(LOGIN_ATTRIBUTE, login);
        necessaryAttributes.put(EMAIL_ATTRIBUTE, email);
        necessaryAttributes.put("image_url", imageUrl);
        return necessaryAttributes;
    }


    private OAuth2User signUpOrUpdateUser(String login, String email, String imageUrl, String username,
        Optional<User> userOptional, Map<String, Object> necessaryAttributes) {
        OAuth2User oAuth2User;
        User user;
        //회원가입, 중복 회원가입 예외 처리 필요할 것으로 보임.
        if (userOptional.isEmpty()) {
            //회원에 필용한 정보 생성 및 조회
            String encodedPassword = passwordEncoder.encode(UUID.randomUUID().toString());

            Member member = Member.of(login);
            memberRepository.save(member);

            Role role = roleRepository.findByValue(RoleEnum.ROLE_USER).orElseThrow(() ->
                new EntityNotFoundException(RoleEnum.ROLE_USER + "에 해당하는 Role이 없습니다."));
            user = User.of(username, encodedPassword, email, login, imageUrl, member);
            UserRole userRole = UserRole.of(role, user);

            userRepository.save(user);
            userRoleRepository.save(userRole);
            necessaryAttributes.put(CREATE_FLAG, true);
            //생성해야할 객체 추가로 더 있을 수 있음.
        } else{
            //회원정보 수정
            user = userOptional.get();
            // 새로 로그인 시 oauth2 기반 데이터로 변경하지않음.
//            user.updateUserBHOAuthIfo(imageUrl);
            necessaryAttributes.put(CREATE_FLAG, false);
        }
        oAuth2User = CustomAuthenticationPrincipal.of(user, necessaryAttributes);
        return oAuth2User;
    }

}
