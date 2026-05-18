package app;

import one.xis.auth.UserInfo;
import one.xis.auth.UserInfoImpl;
import one.xis.auth.UserInfoService;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class SecurityUserInfoService implements UserInfoService<UserInfo> {

    private final Map<String, String> passwords = Map.of(
            "alice", "secret",
            "editor", "secret",
            "totpAlice", "secret",
            "totpEditor", "secret",
            "totpSetupAlice", "secret",
            "totpSetupBob", "secret",
            "totpSetupFlow", "secret",
            "totpWrongPassword", "secret"
    );

    private final Map<String, UserInfo> users = Map.of(
            "alice", user("alice", Set.of("USER")),
            "editor", user("editor", Set.of("USER", "DATA_EDITOR")),
            "totpAlice", user("totpAlice", Set.of("USER")),
            "totpEditor", user("totpEditor", Set.of("USER")),
            "totpSetupAlice", user("totpSetupAlice", Set.of("USER")),
            "totpSetupBob", user("totpSetupBob", Set.of("USER")),
            "totpSetupFlow", user("totpSetupFlow", Set.of("USER")),
            "totpWrongPassword", user("totpWrongPassword", Set.of("USER"))
    );

    @Override
    public boolean validateCredentials(String userId, String password) {
        return passwords.getOrDefault(userId, "").equals(password);
    }

    @Override
    public Optional<UserInfo> getUserInfo(String userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public void saveUserInfo(UserInfo userInfo) {
        throw new UnsupportedOperationException("E2E users are static");
    }

    private static UserInfo user(String userId, Set<String> roles) {
        var user = new UserInfoImpl();
        user.setUserId(userId);
        user.setPreferredUsername(userId);
        user.setRoles(roles);
        return user;
    }
}
