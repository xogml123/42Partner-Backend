package com.seoul.openproject.partner.config.security;

import com.seoul.openproject.partner.domain.model.member.Member;
import com.seoul.openproject.partner.domain.model.user.Role;
import com.seoul.openproject.partner.domain.model.user.RoleEnum;
import com.seoul.openproject.partner.domain.model.user.User;
import com.seoul.openproject.partner.domain.model.user.UserRole;
import com.seoul.openproject.partner.domain.repository.member.MemberRepository;
import com.seoul.openproject.partner.domain.repository.user.RoleRepository;
import com.seoul.openproject.partner.domain.repository.user.UserRepository;
import com.seoul.openproject.partner.domain.repository.user.UserRoleRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Transient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        Map<String, Object> attributes = super.loadUser(userRequest).getAttributes();
        //resource Server로 부터 받아온 정보중 필요한 정보 출출.
        String apiId = (String) attributes.get("id");
        //takim
        String login = (String)attributes.get("login");
        //takim@student.42seoul.kr
        String email = (String) attributes.get("email");
        //https://cdn.intra.42.fr/users/0f260cc3e59777f0f5ba926f19cc1ec9/takim.jpg
        String imageUrl = (String) attributes.get("image_url");

        HashMap<String, Object> necessaryAttributes = new HashMap<>();
        createNeccessaryAttributes(apiId, login, email, imageUrl, necessaryAttributes);

        String username = email;
        Optional<User> userOptional = userRepository.findByUsername(username);
        User oAuth2User = signUpOrUpdateUser(login, email, imageUrl, username, userOptional);
        oAuth2User.getAttributes().putAll(necessaryAttributes);
        return oAuth2User;
    }

    private void createNeccessaryAttributes(String apiId, String login, String email, String imageUrl,
        HashMap<String, Object> necessaryAttributes) {
        necessaryAttributes.put("id", apiId);
        necessaryAttributes.put("login", login);
        necessaryAttributes.put("email", email);
        necessaryAttributes.put("image_url", imageUrl);
    }

    private User signUpOrUpdateUser(String login, String email, String imageUrl, String username,
        Optional<User> userOptional) {
        User user;
        //회원가입
        if (userOptional.isEmpty()) {
            //회원에 필용한 정보 생성 및 조회
            String encodedPassword = passwordEncoder.encode(UUID.randomUUID().toString());
            Role role = roleRepository.findByValue(RoleEnum.ROLE_USER).orElseThrow(() ->
                new EntityNotFoundException(RoleEnum.ROLE_USER + "에 해당하는 Role이 없습니다."));
            UserRole userRole = userRoleRepository.save(UserRole.createUserRole(role));
            Member member = Member.createMember(login);
            memberRepository.save(member);
            user = User.createDefaultUser(username, encodedPassword, email, login, imageUrl, userRole, member);
            userRepository.save(user);
        } else{
            //회원정보 수정
            user = userOptional.get();
            user.updateUserByOAuthIfo(imageUrl);
        }
        return user;
    }

}
