package com.alancortez.project.controller;

import com.alancortez.project.model.Report;
import com.alancortez.project.model.User;
import com.alancortez.project.repository.ReportRepository;
import com.alancortez.project.service.UserService;
import com.alancortez.project.utils.ReportType;
import com.alancortez.project.utils.USER_ROLE;
import com.alancortez.project.utils.UserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/seed")
public class SeedDataController {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/reports")
    public ResponseEntity<String> seedReports() {
        List<Report> reports = new ArrayList<>();
        Random random = new Random();

        String[] recipeNames = {
                "Spaghetti Carbonara", "Chicken Tikka Masala", "Beef Tacos",
                "Caesar Salad", "Margherita Pizza", "Pad Thai",
                "Grilled Salmon", "Vegetable Stir Fry", "Chicken Curry",
                "Beef Stroganoff", "Mushroom Risotto", "Fish and Chips"
        };

        String[] ingredientNames = {
                "Tomatoes", "Chicken Breast", "Onions", "Garlic",
                "Pasta", "Rice", "Olive Oil", "Salt", "Pepper",
                "Cheese", "Lettuce", "Carrots", "Potatoes", "Beef",
                "Fish", "Mushrooms", "Bell Peppers", "Spinach"
        };

        LocalDateTime now = LocalDateTime.now();

        for (int day = 0; day < 30; day++) {
            LocalDateTime date = now.minusDays(day);
            int recipesPerDay = 3 + random.nextInt(8);

            for (int i = 0; i < recipesPerDay; i++) {
                int recipeIndex = random.nextInt(recipeNames.length);
                Report report = new Report(
                        ReportType.RECIPE_USED,
                        (long) (recipeIndex + 1),
                        recipeNames[recipeIndex]
                );
                report.setTimestamp(date.minusHours(random.nextInt(24)));
                reports.add(report);
            }
        }

        for (int day = 0; day < 30; day++) {
            LocalDateTime date = now.minusDays(day);
            int ingredientsPerDay = 5 + random.nextInt(15);

            for (int i = 0; i < ingredientsPerDay; i++) {
                int ingredientIndex = random.nextInt(ingredientNames.length);
                Report report = new Report(
                        ReportType.INGREDIENT_USED,
                        (long) (ingredientIndex + 1),
                        ingredientNames[ingredientIndex]
                );
                report.setTimestamp(date.minusHours(random.nextInt(24)));
                reports.add(report);
            }
        }

        for (int day = 0; day < 30; day++) {
            if (random.nextInt(100) < 30) {
                LocalDateTime date = now.minusDays(day);
                int ingredientIndex = random.nextInt(ingredientNames.length);
                Report report = new Report(
                        ReportType.TIMES_INGREDIENT_REACHED_LOW,
                        (long) (ingredientIndex + 1),
                        ingredientNames[ingredientIndex]
                );
                report.setTimestamp(date.minusHours(random.nextInt(24)));
                reports.add(report);
            }
        }

        for (int day = 0; day < 30; day++) {
            if (random.nextInt(100) < 40) {
                LocalDateTime date = now.minusDays(day);
                int recipesCreated = 1 + random.nextInt(3);

                for (int i = 0; i < recipesCreated; i++) {
                    int recipeIndex = random.nextInt(recipeNames.length);
                    Report report = new Report(
                            ReportType.RECIPES_CREATED,
                            (long) (recipeIndex + 1),
                            recipeNames[recipeIndex]
                    );
                    report.setTimestamp(date.minusHours(random.nextInt(24)));
                    reports.add(report);
                }
            }
        }

        for (int day = 0; day < 30; day++) {
            if (random.nextInt(100) < 35) {
                LocalDateTime date = now.minusDays(day);
                int ingredientsCreated = 1 + random.nextInt(4);

                for (int i = 0; i < ingredientsCreated; i++) {
                    int ingredientIndex = random.nextInt(ingredientNames.length);
                    Report report = new Report(
                            ReportType.INGREDIENTS_CREATED,
                            (long) (ingredientIndex + 1),
                            ingredientNames[ingredientIndex]
                    );
                    report.setTimestamp(date.minusHours(random.nextInt(24)));
                    reports.add(report);
                }
            }
        }

        reportRepository.saveAll(reports);

        return ResponseEntity.ok("Successfully generated " + reports.size() + " report entries!");
    }

    @DeleteMapping("/reports/clear")
    public ResponseEntity<String> clearReports() {
        reportRepository.deleteAll();
        return ResponseEntity.ok("All reports cleared!");
    }

    @PostMapping("/users")
    public ResponseEntity<String> seedUsers() {
        UserFactory userFactory = UserFactory.getInstance();
        List<User> users = new ArrayList<>();

        String[] adminUsernames = {"admin1", "admin2", "admin3"};
        for (String username : adminUsernames) {
            User admin = userFactory.createUser(
                    username,
                    "password123",
                    USER_ROLE.ADMIN
            );
            users.add(admin);
        }

        String[] staffUsernames = {
                "john_doe", "jane_smith", "mike_johnson", "sarah_williams",
                "david_brown", "emily_davis", "james_wilson", "lisa_moore",
                "robert_taylor", "maria_anderson"
        };

        for (String username : staffUsernames) {
            User staff = userFactory.createUser(
                    username,
                    "password123",
                    USER_ROLE.STAFF
            );
            users.add(staff);
        }

        int savedCount = 0;
        for (User user : users) {
            try {
                userService.createUser(user);
                savedCount++;
            } catch (Exception e) {
                System.out.println("Skipped user: " + user.getUsername() + " - " + e.getMessage());
            }
        }

        return ResponseEntity.ok(
                "Successfully created " + savedCount + " users! " +
                        "(Admins: 3, Staff: " + (savedCount - 3) + ")"
        );
    }

    @DeleteMapping("/users/clear")
    public ResponseEntity<String> clearUsers() {
        List<User> allUsers = userService.getAllUsers();
        int count = allUsers.size();

        for (User user : allUsers) {
            userService.deleteUser(user.getId());
        }

        return ResponseEntity.ok("Cleared " + count + " users!");
    }

    @PostMapping("/all")
    public ResponseEntity<String> seedAll() {
        StringBuilder result = new StringBuilder();

        ResponseEntity<String> usersResponse = seedUsers();
        result.append(usersResponse.getBody()).append(" | ");

        ResponseEntity<String> reportsResponse = seedReports();
        result.append(reportsResponse.getBody());

        return ResponseEntity.ok(result.toString());
    }
}
