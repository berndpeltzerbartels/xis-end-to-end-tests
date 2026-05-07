package app;

import one.xis.auth.LocalKeyProvider;
import one.xis.context.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Component
public class DistributedSsoLocalKeyProvider implements LocalKeyProvider {

    private static final String KEY_ID = "distributed-sso-key";
    private static final Map<String, KeyPair> KEYS = Map.of(KEY_ID, createKeyPair());

    @Override
    public KeyPair getKeyPair(String keyId) {
        return KEYS.get(keyId);
    }

    @Override
    public Collection<String> getKeyIds() {
        return Set.of(KEY_ID);
    }

    private static KeyPair createKeyPair() {
        try {
            var random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed("xis-distributed-sso-e2e-key".getBytes(java.nio.charset.StandardCharsets.UTF_8));
            var generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048, random);
            return generator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException("Could not create distributed SSO E2E key pair", e);
        }
    }
}
