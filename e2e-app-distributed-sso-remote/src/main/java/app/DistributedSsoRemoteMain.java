package app;

import one.xis.boot.XISBootApplication;
import one.xis.boot.XISBootRunner;

@XISBootApplication
public class DistributedSsoRemoteMain {
    public static void main(String[] args) {
        XISBootRunner.run(DistributedSsoRemoteMain.class, args);
    }
}
