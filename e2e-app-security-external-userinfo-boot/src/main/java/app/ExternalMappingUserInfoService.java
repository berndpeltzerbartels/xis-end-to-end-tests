package app;

import one.xis.auth.UserInfoImpl;
import one.xis.auth.UserInfoService;
import one.xis.context.Component;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class ExternalMappingUserInfoService implements UserInfoService<UserInfoImpl> {

    private final ConcurrentMap<String, UserInfoImpl> users = new ConcurrentHashMap<>();

    @Override
    public boolean supportsLocalLogin() {
        return false;
    }

    @Override
    public boolean validateCredentials(String userId, String password) {
        return false;
    }

    @Override
    public Optional<UserInfoImpl> getUserInfo(String userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public void saveUserInfo(UserInfoImpl userInfo) {
        userInfo.setRoles(Set.of("MAPPED_USER"));
        users.put(userInfo.getUserId(), userInfo);
    }
}
