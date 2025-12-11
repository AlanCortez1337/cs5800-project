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

    /**
     * Generate seed data for demonstration
     * POST /api/reports/seed
     */
    @PostMapping("/reports")
    public ResponseEntity<String> seedReports() {
        List<Report> reports = new ArrayList<>();
        Random random = new Random();

        // Sample recipe names
        String[] recipeNames = {
                "Spaghetti Carbonara", "Chicken Tikka Masala", "Beef Tacos",
                "Caesar Salad", "Margherita Pizza", "Pad Thai",
                "Grilled Salmon", "Vegetable Stir Fry", "Chicken Curry",
                "Beef Stroganoff", "Mushroom Risotto", "Fish and Chips"
        };

        // Sample ingredient names
        String[] ingredientNames = {
                "Tomatoes", "Chicken Breast", "Onions", "Garlic",
                "Pasta", "Rice", "Olive Oil", "Salt", "Pepper",
                "Cheese", "Lettuce", "Carrots", "Potatoes", "Beef",
                "Fish", "Mushrooms", "Bell Peppers", "Spinach"
        };

        LocalDateTime now = LocalDateTime.now();

        // Generate RECIPE_USED data for last 30 days
        for (int day = 0; day < 30; day++) {
            LocalDateTime date = now.minusDays(day);
            int recipesPerDay = 3 + random.nextInt(8); // 3-10 recipes per day

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

        // Generate INGREDIENT_USED data
        for (int day = 0; day < 30; day++) {
            LocalDateTime date = now.minusDays(day);
            int ingredientsPerDay = 5 + random.nextInt(15); // 5-20 ingredients per day

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

        // Generate TIMES_INGREDIENT_REACHED_LOW data (less frequent)
        for (int day = 0; day < 30; day++) {
            if (random.nextInt(100) < 30) { // 30% chance per day
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

        // Generate RECIPES_CREATED data (less frequent)
        for (int day = 0; day < 30; day++) {
            if (random.nextInt(100) < 40) { // 40% chance per day
                LocalDateTime date = now.minusDays(day);
                int recipesCreated = 1 + random.nextInt(3); // 1-3 recipes

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

        // Generate INGREDIENTS_CREATED data (less frequent)
        for (int day = 0; day < 30; day++) {
            if (random.nextInt(100) < 35) { // 35% chance per day
                LocalDateTime date = now.minusDays(day);
                int ingredientsCreated = 1 + random.nextInt(4); // 1-4 ingredients

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

        // Save all reports
        reportRepository.saveAll(reports);

        return ResponseEntity.ok("Successfully generated " + reports.size() + " report entries!");
    }

    /**
     * Clear all reports (for testing)
     * DELETE /api/reports/clear
     */
    @DeleteMapping("/reports/clear")
    public ResponseEntity<String> clearReports() {
        reportRepository.deleteAll();
        return ResponseEntity.ok("All reports cleared!");
    }

    @PostMapping("/users")
    public ResponseEntity<String> seedUsers() {
        UserFactory userFactory = UserFactory.getInstance();
        List<User> users = new ArrayList<>();

        // Create 3 Admin users
        String[] adminUsernames = {"admin1", "admin2", "admin3"};
        for (String username : adminUsernames) {
            User admin = userFactory.createUser(
                    username,
                    "password123", // In production, use hashed passwords
                    USER_ROLE.ADMIN
            );
            users.add(admin);
        }

        // Create 10 Staff users with varied names
        String[] staffUsernames = {
                "john_doe", "jane_smith", "mike_johnson", "sarah_williams",
                "david_brown", "emily_davis", "james_wilson", "lisa_moore",
                "robert_taylor", "maria_anderson"
        };

        for (String username : staffUsernames) {
            User staff = userFactory.createUser(
                    username,
                    "password123", // In production, use hashed passwords
                    USER_ROLE.STAFF
            );
            users.add(staff);
        }

        // Save all users
        int savedCount = 0;
        for (User user : users) {
            try {
                userService.createUser(user);
                savedCount++;
            } catch (Exception e) {
                // Skip if user already exists or other error
                System.out.println("Skipped user: " + user.getUsername() + " - " + e.getMessage());
            }
        }

        return ResponseEntity.ok(
                "Successfully created " + savedCount + " users! " +
                        "(Admins: 3, Staff: " + (savedCount - 3) + ")"
        );
    }

    /**
     * Clear all users (for testing)
     * DELETE /api/seed/users/clear
     */
    @DeleteMapping("/users/clear")
    public ResponseEntity<String> clearUsers() {
        List<User> allUsers = userService.getAllUsers();
        int count = allUsers.size();

        for (User user : allUsers) {
            userService.deleteUser(user.getId());
        }

        return ResponseEntity.ok("Cleared " + count + " users!");
    }

    /**
     * Seed everything at once
     * POST /api/seed/all
     */
    @PostMapping("/all")
    public ResponseEntity<String> seedAll() {
        StringBuilder result = new StringBuilder();

        // Seed users first
        ResponseEntity<String> usersResponse = seedUsers();
        result.append(usersResponse.getBody()).append(" | ");

        // Seed reports
        ResponseEntity<String> reportsResponse = seedReports();
        result.append(reportsResponse.getBody());

        return ResponseEntity.ok(result.toString());
    }
}
