package app;

import one.xis.UploadConfiguration;
import one.xis.context.Component;

@Component
public class UploadTestConfiguration implements UploadConfiguration {

    @Override
    public long getMaxFileSize() {
        return 10;
    }

    @Override
    public long getMaxRequestSize() {
        return 4096;
    }
}
