package SpringSecurity.SpringSecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringSecurityApplication {

    public static final String SECRET_KEY =
        "MIIBOQIBAAJAdUMfrhdOCKRcPsvfuqMgbuDFN7S6ANQ8zJ342pTh25zvtm+AfInu\n"
            + "ERWNaYvvEGQBSpiIvn8pPYYUFqyemedAfwIDAQABAkBioQG9eUaeSDh6bRqSLfDy\n"
            + "scXP9Afa+Kjm5hxRJZbPEwv3NXUwWkQmIMvyF4GeXalM7ptLE/nJJwjwl9tBjWZp\n"
            + "AiEAxoHgg08L7aRubK5roBTVQcMpiU+rFm3b71NOYxdOfxsCIQCXOWjkHmMkZEB2\n"
            + "tqR6XnVVDLUcD5zTwvDB83uHTQmGbQIgMomsTCHaypS7XEYHtoi3RnfWYlP7Hcm4\n"
            + "XbTa1xV+8ZMCIFOMdFcUMtI4USVFYzn4VfrXOnh2Z77XRQzi6Kyn80tBAiEAkC5k\n"
            + "8ROlJsj4bgGA/kFb/ZqGRPEx6dwRfklUH/EuG0U=";

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityApplication.class, args);
    }

}
