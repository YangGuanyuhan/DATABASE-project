package io.sustc.dto;

import io.pubmed.dto.*;
import io.pubmed.service.impl.AuthorServiceImpl;
import io.pubmed.service.impl.ArticleServiceImpl;
import io.pubmed.service.impl.JournalServiceImpl;
import io.pubmed.service.impl.KeywordServiceImpl;
import io.pubmed.service.impl.GrantServiceImpl;
import io.pubmed.service.impl.UserServicelmpl;
import io.pubmed.service.impl.DatabaseServiceImpl;

import java.util.List;
import java.util.Scanner;

public class MainProgram {

    // Assuming we have these service instances
    private static DatabaseServiceImpl databaseService = new DatabaseServiceImpl();
    private static AuthorServiceImpl authorService = new AuthorServiceImpl();
    private static ArticleServiceImpl articleService = new ArticleServiceImpl();
    private static JournalServiceImpl journalService = new JournalServiceImpl();
    private static KeywordServiceImpl keywordService = new KeywordServiceImpl();
    private static GrantServiceImpl grantService = new GrantServiceImpl();
    private static UserServicelmpl userServicelmpl = new UserServicelmpl();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Receive login or registration action
        System.out.println("Enter action: login or register? (login/register)");
        String action = scanner.nextLine().toLowerCase();

        if ("login".equals(action)) {
            // Login operation
            System.out.println("Enter ID:");
            int id = Integer.parseInt(scanner.nextLine());
            System.out.println("Enter username:");
            String username = scanner.nextLine();
            System.out.println("Enter password:");
            String password = scanner.nextLine();

            int loginResult = userServicelmpl.findFirstDigitOfUserId(id, username, password); // Call login method, assuming the result is a number indicating login status

            if (loginResult != 0) {
                System.out.println("Login successful, your identity is: " + loginResult);
                // Limit interface access based on identity
                boolean canUseArticle = true;
                boolean canUseJournal = true;

                if (loginResult == 3 || loginResult == 5) {
                    canUseArticle = false;  // If identity is 3 or 5, can't use article methods
                    canUseJournal = false;  // If identity is 3 or 5, can't use journal methods
                } else if (loginResult == 1) {
                    canUseArticle = false;  // If identity is 1, can't use article methods
                } else if (loginResult == 2) {
                    canUseJournal = false;  // If identity is 2, can't use journal methods
                }

                // Infinite loop for input commands
                while (true) {
                    System.out.println("Enter the interface method to call:");
                    String methodName = scanner.nextLine();

                    // Select interface method based on user input
                    switch (methodName.toLowerCase()) {
                        // Article Interface
                        case "addarticleandupdateif":
                            if (canUseArticle) {
                                System.out.println("Enter article title:");
                                String title = scanner.nextLine();
                                System.out.println("Enter impact factor:");
                                double impactFactor = scanner.nextDouble();
                                scanner.nextLine(); // Consume newline

                                // Create Article object and call service method
                                Article article = new Article();
                                article.setTitle(title);

                                double updatedImpactFactor = articleService.addArticleAndUpdateIF(article);
                                System.out.println("Article added and impact factor updated, new impact factor: " + updatedImpactFactor);
                            } else {
                                System.out.println("You do not have permission to access this interface");
                            }
                            break;

                        case "getarticlecountbykeywordinpastyears":
                            System.out.println("Enter keyword:");
                            String keyword = scanner.nextLine();
                            int[] articleCounts = keywordService.getArticleCountByKeywordInPastYears(keyword);
                            System.out.println("Article count by year:");
                            for (int count : articleCounts) {
                                System.out.println(count);
                            }
                            break;

                        // Author Interface
                        case "getarticlesbyauthorsortedbycitations":
                            System.out.println("Enter author's name (firstName lastName):");
                            String name = scanner.nextLine();
                            String[] nameParts = name.split(" ");
                            String foreName = nameParts[0];
                            String lastName = nameParts[1];
                            Author author = new Author();
                            author.setFore_name(foreName);
                            author.setLast_name(lastName);
                            int[] articles = authorService.getArticlesByAuthorSortedByCitations(author);
                            System.out.println("Retrieved article IDs: " + articles);
                            break;

                        case "getjournalwithmostarticlesbyauthor":
                            System.out.println("Enter author's name (firstName lastName):");
                            String authorName = scanner.nextLine();
                            String[] authorNameParts = authorName.split(" ");
                            String foreName1 = authorNameParts[0];
                            String lastName1 = authorNameParts[1];
                            Author author1 = new Author();
                            author1.setFore_name(foreName1);
                            author1.setLast_name(lastName1);
                            String journal = authorService.getJournalWithMostArticlesByAuthor(author1);
                            System.out.println("The journal with the most articles by this author is: " + journal);
                            break;

                        // Journal Interface
                        case "updatejournalname":
                            if (canUseJournal) {
                                System.out.println("Enter journal ID:");
                                String journalId = scanner.nextLine();
                                System.out.println("Enter new journal name:");
                                String newName = scanner.nextLine();
                                System.out.println("Enter new journal ID:");
                                String newId = scanner.nextLine();
                                Journal journal1 = new Journal();
                                journal1.setId(journalId);
                                boolean success = journalService.updateJournalName(journal1, 2024, newName, newId);
                                if (success) {
                                    System.out.println("Journal name updated");
                                } else {
                                    System.out.println("Failed to update journal name");
                                }
                            } else {
                                System.out.println("You do not have permission to access this interface");
                            }
                            break;

                        case "getimpactfactor":
                            System.out.println("Enter journal title:");
                            String journalTitle = scanner.nextLine();
                            System.out.println("Enter year:");
                            int year = scanner.nextInt();
                            double impactFactor = journalService.getImpactFactor(journalTitle, year);
                            System.out.println("Journal impact factor: " + impactFactor);
                            break;

                        // Grants Interface
                        case "getcountryfundpapers":
                            System.out.println("Enter country name:");
                            String country = scanner.nextLine();
                            int[] papers = grantService.getCountryFundPapers(country);
                            System.out.println("Papers funded by the country:");
                            for (int paper : papers) {
                                System.out.println(paper);
                            }
                            break;

                        case "getgroupmembers":
                            if (true) {  // Assuming a flag to check permission
                                List<Integer> groupMembers = databaseService.getGroupMembers();  // Call getGroupMembers method
                                System.out.println("Group member IDs: " + groupMembers);
                            } else {
                                System.out.println("You do not have permission to access this interface");
                            }
                            break;

                        case "getarticlecountbykeyword":
                            if (true) {  // Assuming a flag to check permission
                                System.out.println("Enter keyword:");
                                String keyword1 = scanner.nextLine();
                                int[] articleCounts1 = keywordService.getArticleCountByKeywordInPastYears(keyword1);  // Call this method
                                if (articleCounts1.length > 0) {
                                    System.out.println("Article count with the keyword in past years:");
                                    for (int count : articleCounts1) {
                                        System.out.println(count);
                                    }
                                } else {
                                    System.out.println("No related article data found");
                                }
                            } else {
                                System.out.println("You do not have permission to access this interface");
                            }
                            break;

                        default:
                            System.out.println("Invalid interface method, please enter again");
                            break;
                    }
                }
            } else {
                System.out.println("Login failed, please check your username and password");
            }

        } else if ("register".equals(action)) {
            // Registration operation
            System.out.println("Enter username:");
            String username = scanner.nextLine();
            System.out.println("Enter password:");
            String password = scanner.nextLine();

            boolean registrationResult = register(username, password);
            if (registrationResult) {
                System.out.println("Registration successful, please log in");
            } else {
                System.out.println("Registration failed, please try again");
            }

        } else {
            System.out.println("Invalid operation, please enter login or register");
        }

        scanner.close();
    }

    // Simulate login method
    public static int login(String username, String password) {
        // Assuming successful username and password validation, return different identity numbers
        if ("admin".equals(username) && "admin123".equals(password)) {
            return 1;  // Return identity 1
        } else if ("user".equals(username) && "user123".equals(password)) {
            return 2;  // Return identity 2
        } else if ("guest".equals(username) && "guest123".equals(password)) {
            return 3;  // Return identity 3
        } else {
            return 0;  // Login failed
        }
    }

    // Simulate registration method
    public static boolean register(String username, String password) {
        // Assuming registration is successful
        return true;
    }
}
