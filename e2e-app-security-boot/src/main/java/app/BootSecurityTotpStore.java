package app;

import one.xis.context.Component;
import one.xis.totp.TOTPStore;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BootSecurityTotpStore implements TOTPStore {

    private final Map<String, String> encryptedSecrets = new ConcurrentHashMap<>();
    private final Map<String, Long> acceptedSteps = new ConcurrentHashMap<>();

    @Override
    public Optional<String> getEncryptedSecret(String userId) {
        return Optional.ofNullable(encryptedSecrets.get(userId));
    }

    @Override
    public void saveEncryptedSecret(String userId, String encryptedSecret) {
        encryptedSecrets.put(userId, encryptedSecret);
    }

    @Override
    public OptionalLong getLastAcceptedTimeStep(String userId) {
        Long step = acceptedSteps.get(userId);
        return step == null ? OptionalLong.empty() : OptionalLong.of(step);
    }

    @Override
    public void saveLastAcceptedTimeStep(String userId, long timeStep) {
        acceptedSteps.put(userId, timeStep);
    }
}
