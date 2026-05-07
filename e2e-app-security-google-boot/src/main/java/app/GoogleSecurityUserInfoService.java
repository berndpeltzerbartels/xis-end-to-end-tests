package app;

import one.xis.auth.UserInfoImpl;
import one.xis.auth.UserInfoService;
import one.xis.context.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GoogleSecurityUserInfoService implements UserInfoService<UserInfoImpl> {

    private final Map<String, UserInfoImpl> users = new ConcurrentHashMap<>();

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
        userInfo.setRoles(Set.of("USER"));
        users.put(userInfo.getUserId(), userInfo);
    }
}
