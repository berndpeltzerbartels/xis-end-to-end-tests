package app;

import one.xis.boot.XISBootApplication;
import one.xis.boot.XISBootRunner;

@XISBootApplication
public class DistributedSsoShellMain {
    public static void main(String[] args) {
        XISBootRunner.run(DistributedSsoShellMain.class, args);
    }
}
