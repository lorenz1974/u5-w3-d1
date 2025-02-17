package u5w2d5.etm.auth.runner;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import u5w2d5.etm.auth.model.AppUserRole;
import u5w2d5.etm.auth.service.AppUserService;

@Order(1)
@Component
@Slf4j
@RequiredArgsConstructor
public class CreateAppUsers implements CommandLineRunner {

    private final Faker faker;
    private final AppUserService appUserService;

    @Override
    public void run(String... args) throws Exception {

        log.info("Creating application users...");

        log.info("Creating 3 admin users...");
        for (int i = 0; i < 3; i++) {
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String userName = (firstName + "_" + lastName).toLowerCase();

            String email = firstName.replace(" ", "") +
                    "." +
                    lastName.replace(" ", "") +
                    "@" +
                    faker.internet().domainName().replace(" ", "").toLowerCase();
            String password = "admin";
            Set<AppUserRole> roles = Set.of(AppUserRole.ROLE_ADMIN);

            appUserService.registerUser(firstName, lastName, userName, email, password, roles);
        }

        log.info("Creating 3 seller users...");
        for (int i = 0; i < 3; i++) {
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String userName = (firstName + "_" + lastName).toLowerCase();
            String email = firstName.replace(" ", "") +
                    "." +
                    lastName.replace(" ", "") +
                    "@" +
                    faker.internet().domainName().replace(" ", "").toLowerCase();
            String password = "seller";
            Set<AppUserRole> roles = Set.of(AppUserRole.ROLE_SELLER);

            appUserService.registerUser(firstName, lastName, userName, email, password, roles);
        }

        log.info("Creating 3 buyer users...");
        for (int i = 0; i < 3; i++) {
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String userName = (firstName + "_" + lastName).toLowerCase();
            String email = firstName.replace(" ", "") +
                    "." +
                    lastName.replace(" ", "") +
                    "@" +
                    faker.internet().domainName().replace(" ", "").toLowerCase();
            String password = "seller";
            Set<AppUserRole> roles = Set.of(AppUserRole.ROLE_BUYER);

            appUserService.registerUser(firstName, lastName, userName, email, password, roles);
        }

        log.info("Creating 3 seller&buyer users...");
        for (int i = 0; i < 3; i++) {
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String userName = (firstName + "_" + lastName).toLowerCase();
            String email = firstName.replace(" ", "") +
                    "." +
                    lastName.replace(" ", "") +
                    "@" +
                    faker.internet().domainName().replace(" ", "").toLowerCase();
            String password = "buyer";
            Set<AppUserRole> roles = Set.of(AppUserRole.ROLE_SELLER, AppUserRole.ROLE_BUYER);

            appUserService.registerUser(firstName, lastName, userName, email, password, roles);
        }

        log.info("Creating 3 default users...");
        for (int i = 0; i < 3; i++) {
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String userName = (firstName + "_" + lastName).toLowerCase();
            String email = firstName.replace(" ", "") +
                    "." +
                    lastName.replace(" ", "") +
                    "@" +
                    faker.internet().domainName().replace(" ", "").toLowerCase();
            String password = "user";
            Set<AppUserRole> roles = Set.of(AppUserRole.ROLE_USER);

            appUserService.registerUser(firstName, lastName, userName, email, password, roles);
        }

        log.info("Application users created successfully.");
    }
}