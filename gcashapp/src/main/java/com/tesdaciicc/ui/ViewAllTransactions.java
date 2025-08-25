package com.tesdaciicc.ui;

import com.tesdaciicc.model.Transactions;
import com.tesdaciicc.model.UserAuthentication;
import com.tesdaciicc.service.TransactionsService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ViewAllTransactions {

    private static Scanner scanner = new Scanner(System.in);

    private ViewAllTransactions() {
       // Private constructor to prevent instantiation
    }

    public static void viewAllTransactions(UserAuthentication user) {
        System.out.println("\n>>>     View All Transactions     <<<");
        System.out.println("Welcome, " + user.getName());
        viewUserAllTransactions(user);
    }
        
    private static void viewUserAllTransactions(UserAuthentication user) {
        TransactionsService transactionsService = new TransactionsService();
        boolean viewTransactionsMenu = true;
            
        while (viewTransactionsMenu) {
         System.out.println("1.    View All My Transactions");
         System.out.println("2.    View Specific Transaction by ID");
         System.out.println("3.    View Recent Transactions (Last 30 days)");
         System.out.println("4.    View Transaction Statistics");
         System.out.println("5.    View Transactions (Paginated)");
         System.out.println("6.    Search Transactions by Name");
         System.out.println("7.    Back to Main Menu");
         System.out.print("Choose option:   ");    
                
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                    
                switch (choice) {
                    case 1 -> viewAllUserTransactions(user, transactionsService);
                    case 2 -> viewSpecificTransaction(user, transactionsService);
                    case 3 -> viewRecentTransactions(user, transactionsService);
                    case 4 -> viewTransactionStatistics(user, transactionsService);
                    case 5 -> viewPaginatedTransactions(user, transactionsService);
                    case 6 -> searchTransactionsByName(user, transactionsService);
                    case 7 -> viewTransactionsMenu = false;
                    default -> System.out.println("Invalid option! Please try again.");
                }
                    
                if (viewTransactionsMenu && choice != 7) {
                    System.out.println("\nPress Enter to continue...");
                    scanner.nextLine();
                }
                    
            } catch (Exception e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    /**
     * View all transactions for the current user
     */
    private static void viewAllUserTransactions(UserAuthentication user, TransactionsService service) {
        System.out.println("\n>>>   All My Transactions   <<<");
        
        try {
            List<Transactions> transactions = service.viewUserAll(user.getId());
            
            if (transactions.isEmpty()) {
                System.out.println("No transactions found for your account.");
                return;
            }
            
            System.out.println("Total Transactions: " + transactions.size());
            System.out.println("=" .repeat(120));
            System.out.printf("%-5s | %-15s | %-20s | %-12s | %-20s | %-15s | %-15s%n",
                            "ID", "Amount", "Name", "Type", "Date", "From Account", "To Account");
            System.out.println("=" .repeat(120));
            
            for (Transactions transaction : transactions) {
                System.out.printf("%-5d | %-15s | %-20s | %-12s | %-20s | %-15s | %-15s%n",
                    transaction.getTransactionId(),
                    transaction.getFormattedAmount(),
                    truncateString(transaction.getTransactionName(), 20),
                    transaction.getTransactionType(),
                    transaction.getFormattedDate(),
                    truncateString(transaction.getTransferFromAccountNo(), 15),
                    truncateString(transaction.getTransferToAccountNo(), 15)
                );
            }
            
            System.out.println("=" .repeat(120));
            
        } catch (Exception e) {
            System.out.println("Error retrieving transactions: " + e.getMessage());
        }
    }


    /**
 * View a specific transaction by ID
 */
    private static void viewSpecificTransaction(UserAuthentication user, TransactionsService service) {
        System.out.println("\n>>>   View Specific Transaction   <<<");
        System.out.print("Enter Transaction ID: ");
        
        try {
            int transactionId = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            Optional<Transactions> transactionOpt = service.viewTransaction(transactionId);
            
            if (transactionOpt.isPresent()) {
                Transactions transaction = transactionOpt.get();
                
                // Check if transaction belongs to current user
                if (transaction.getUserId() != user.getId()) {
                    System.out.println("Transaction not found or you don't have permission to view it.");
                    return;
                }
                
                displayTransactionDetails(transaction);
            } else {
                System.out.println("Transaction with ID " + transactionId + " not found.");
            }
            
        } catch (Exception e) {
            System.out.println("Invalid input! Please enter a valid transaction ID.");
            scanner.nextLine(); // Clear invalid input
        }
    }

    /**
     * View recent transactions (last 30 days)
     */
    private static void viewRecentTransactions(UserAuthentication user, TransactionsService service) {
        System.out.println("\n>>>   Recent Transactions (Last 30 Days)   <<<");
        
        try {
            List<Transactions> recentTransactions = service.getRecentUserTransactions(user.getId());
            
            if (recentTransactions.isEmpty()) {
                System.out.println("No recent transactions found for your account.");
                return;
            }
            
            System.out.println("Recent Transactions: " + recentTransactions.size());
            System.out.println("=" .repeat(100));
            System.out.printf("%-5s | %-15s | %-25s | %-12s | %-20s%n",
                            "ID", "Amount", "Name", "Type", "Date");
            System.out.println("=" .repeat(100));
            
            for (Transactions transaction : recentTransactions) {
                System.out.printf("%-5d | %-15s | %-25s | %-12s | %-20s%n",
                    transaction.getTransactionId(),
                    transaction.getFormattedAmount(),
                    truncateString(transaction.getTransactionName(), 25),
                    transaction.getTransactionType(),
                    transaction.getFormattedDate()
                );
            }
            
            System.out.println("=" .repeat(100));
            
        } catch (Exception e) {
            System.out.println("Error retrieving recent transactions: " + e.getMessage());
        }
    }

    /**
     * View transaction statistics for the user
     */
    private static void viewTransactionStatistics(UserAuthentication user, TransactionsService service) {
        System.out.println("\n>>>   Transaction Statistics   <<<");
        
        try {
            TransactionsService.TransactionStatistics stats = service.getUserTransactionStatistics(user.getId());
            
            System.out.println("User: " + user.getName());
            System.out.println("=" .repeat(60));
            System.out.println("Total Transactions: " + stats.getTotalCount());
            System.out.println("Total Amount: " + stats.getFormattedTotalAmount());
            System.out.println("Average Amount: " + stats.getFormattedAverageAmount());
            System.out.println("Maximum Amount: " + stats.getFormattedMaxAmount());
            System.out.println("Minimum Amount: " + stats.getFormattedMinAmount());
            System.out.println("=" .repeat(60));
            System.out.println("Transaction Types:");
            System.out.println("  - Transfers: " + stats.getTransferCount());
            System.out.println("  - Cash-In: " + stats.getCashInCount());
            System.out.println("  - Cash-Out: " + stats.getCashOutCount());
            System.out.println("  - Other: " + stats.getOtherCount());
            System.out.println("=" .repeat(60));
            
        } catch (Exception e) {
            System.out.println("Error retrieving transaction statistics: " + e.getMessage());
        }
    }

    /**
     * View transactions with pagination
     */
    private static void viewPaginatedTransactions(UserAuthentication user, TransactionsService service) {
        System.out.println("\n>>>   Paginated Transactions   <<<");
        
        final int PAGE_SIZE = 10;
        int currentPage = 1;
        boolean continuePaging = true;
        
        try {
            long totalTransactions = service.getUserTransactionCount(user.getId());
            int totalPages = (int) Math.ceil((double) totalTransactions / PAGE_SIZE);
            
            if (totalTransactions == 0) {
                System.out.println("No transactions found for your account.");
                return;
            }
            
            while (continuePaging) {
                List<Transactions> transactions = service.viewUserAllPaginated(user.getId(), currentPage, PAGE_SIZE);
                
                System.out.println("\nPage " + currentPage + " of " + totalPages + 
                                " (Total: " + totalTransactions + " transactions)");
                System.out.println("=" .repeat(100));
                System.out.printf("%-5s | %-15s | %-25s | %-12s | %-20s%n",
                                "ID", "Amount", "Name", "Type", "Date");
                System.out.println("=" .repeat(100));
                
                for (Transactions transaction : transactions) {
                    System.out.printf("%-5d | %-15s | %-25s | %-12s | %-20s%n",
                        transaction.getTransactionId(),
                        transaction.getFormattedAmount(),
                        truncateString(transaction.getTransactionName(), 25),
                        transaction.getTransactionType(),
                        transaction.getFormattedDate()
                    );
                }
                
                System.out.println("=" .repeat(100));
                System.out.println("\nNavigation Options:");
                if (currentPage > 1) System.out.print("P - Previous Page | ");
                if (currentPage < totalPages) System.out.print("N - Next Page | ");
                System.out.println("Q - Quit Pagination");
                System.out.print("Choose option: ");
                
                String choice = scanner.nextLine().trim().toUpperCase();
                
                switch (choice) {
                    case "P" -> {
                        if (currentPage > 1) {
                            currentPage--;
                        } else {
                            System.out.println("Already on first page.");
                        }
                    }
                    case "N" -> {
                        if (currentPage < totalPages) {
                            currentPage++;
                        } else {
                            System.out.println("Already on last page.");
                        }
                    }
                    case "Q" -> continuePaging = false;
                    default -> System.out.println("Invalid option! Please try again.");
                }
            }
            
        } catch (Exception e) {
            System.out.println("Error retrieving paginated transactions: " + e.getMessage());
        }
    }

    /**
     * Search transactions by name
     */
    private static void searchTransactionsByName(UserAuthentication user, TransactionsService service) {
        System.out.println("\n>>>   Search Transactions by Name   <<<");
        System.out.print("Enter search term: ");
        
        try {
            String searchTerm = scanner.nextLine().trim();
            
            if (searchTerm.isEmpty()) {
                System.out.println("Search term cannot be empty.");
                return;
            }
            
            List<Transactions> allSearchResults = service.searchTransactionsByName(searchTerm);
            
            // Filter results to show only current user's transactions
            List<Transactions> userSearchResults = allSearchResults.stream()
                .filter(t -> t.getUserId() == user.getId())
                .toList();
            
            if (userSearchResults.isEmpty()) {
                System.out.println("No transactions found matching '" + searchTerm + "'");
                return;
            }
            
            System.out.println("\nSearch Results: " + userSearchResults.size() + " transactions found");
            System.out.println("=" .repeat(100));
            System.out.printf("%-5s | %-15s | %-25s | %-12s | %-20s%n",
                            "ID", "Amount", "Name", "Type", "Date");
            System.out.println("=" .repeat(100));
            
            for (Transactions transaction : userSearchResults) {
                System.out.printf("%-5d | %-15s | %-25s | %-12s | %-20s%n",
                    transaction.getTransactionId(),
                    transaction.getFormattedAmount(),
                    highlightSearchTerm(transaction.getTransactionName(), searchTerm, 25),
                    transaction.getTransactionType(),
                    transaction.getFormattedDate()
                );
            }
            
            System.out.println("=" .repeat(100));
            
        } catch (Exception e) {
            System.out.println("Error searching transactions: " + e.getMessage());
        }
    }

    /**
     * Display detailed information for a single transaction
     */
    private static void displayTransactionDetails(Transactions transaction) {
        System.out.println("\n>>>   Transaction Details   <<<");
        System.out.println("=" .repeat(50));
        System.out.println("Transaction ID: " + transaction.getTransactionId());
        System.out.println("Amount: " + transaction.getFormattedAmount());
        System.out.println("Transaction Name: " + transaction.getTransactionName());
        System.out.println("Type: " + transaction.getTransactionType());
        System.out.println("Date: " + transaction.getFormattedDate());
        System.out.println("User ID: " + transaction.getUserId());
        
        if (transaction.getAccountNumber() != null) {
            System.out.println("Account Number: " + transaction.getAccountNumber());
        }
        
        if (transaction.getTransferFromAccountNo() != null) {
            System.out.println("Transfer From: " + transaction.getTransferFromAccountNo());
        }
        
        if (transaction.getTransferToAccountNo() != null) {
            System.out.println("Transfer To: " + transaction.getTransferToAccountNo());
        }
        
        System.out.println("=" .repeat(50));
    }

    /**
     * Helper method to truncate strings for display
     */
    private static String truncateString(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }

    /**
     * Helper method to highlight search terms (simple version)
     */
    private static String highlightSearchTerm(String text, String searchTerm, int maxLength) {
        if (text == null) return "";
        
        String truncated = truncateString(text, maxLength);
        // Simple highlighting by making the search term uppercase
        return truncated.replaceAll("(?i)" + searchTerm, searchTerm.toUpperCase());
    }

}
