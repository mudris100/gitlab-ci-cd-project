package dev.udris.oauth2;

import dev.udris.entity.Role;
import dev.udris.entity.User;
import dev.udris.repository.UserRepository;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;

    public CustomOidcUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
//        String sub = oidcUser.getSubject();
        String email = oidcUser.getAttribute("email");
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String name = oidcUser.getAttribute("name");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(name);
            newUser.setRole(Role.USER);
            newUser.setProvider(registrationId);
            return userRepository.save(newUser);
        });

        CustomUserPrincipal principal = CustomUserPrincipal.fromUser(user, oidcUser.getAttributes());
        principal.setIdToken(oidcUser.getIdToken());
        principal.setUserInfo(oidcUser.getUserInfo());
        return principal;
    }
}

