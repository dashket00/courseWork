import Model.MoneyTransfer;
import Parser.JsonParser;
import Parser.XmlParser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;


public class MoneyTransferApp {
    private static final String REPORT_FILE_NAME = "report.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String directoryPath;

        do {
            System.out.print("Введите путь к каталогу: ");
            directoryPath = scanner.nextLine();

            if (isDirectoryValid(directoryPath)) {
                System.out.println("Неправильный путь. Повторите ввод.");
            }
        } while (isDirectoryValid(directoryPath));
        List<String> errorMessages = new ArrayList<>();
        List<MoneyTransfer> moneyTransfers = parseFiles(directoryPath, true, errorMessages);
        generateReport(moneyTransfers, errorMessages);

        boolean exit = false;
        while (!exit) {
            System.out.println("Меню:");
            System.out.println("1 - Вызов парсера");
            System.out.println("2 - Вывод всех операций");
            System.out.println("0 - Выйти из программы");
            System.out.print("Выберите действие: ");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    moneyTransfers = parseFiles(directoryPath, true, errorMessages);
                    generateReport(moneyTransfers, errorMessages);
                    break;
                case 2:
                    printAllOperations(moneyTransfers);
                    break;
                case 0:
                    exit = true;
                    break;
                default:
                    System.out.println("Некорректный выбор. Попробуйте еще раз.");
            }
        }
    }

    private static boolean isDirectoryValid(String directoryPath) {
        File directory = new File(directoryPath);
        return !directory.exists() || !directory.isDirectory();
    }

    private static List<MoneyTransfer> parseFiles(String directoryPath, boolean isHiddenLogs, List<String> errorMessages) {
        List<MoneyTransfer> moneyTransfers = new ArrayList<>();

        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                try {
                    if (file.getName().toLowerCase().endsWith(".json")) {
                        moneyTransfers.addAll(JsonParser.parseJsonFile(file));
                    } else if (file.getName().toLowerCase().endsWith(".xml")) {
                        moneyTransfers.addAll(XmlParser.parseXmlFile(file));
                    }
                    if (!isHiddenLogs) {
                        logResult(file.getName(), "Успешно обработан");
                    }
                } catch (Exception e) {
                    String errorMessage = "Ошибка во время работы с файлом " + file.getName() + ": " + e.getMessage();
                    logResult(file.getName(), errorMessage);
                    errorMessages.add(errorMessage);
                }
            }
        }
        generateReport(moneyTransfers, errorMessages);
        return moneyTransfers;
    }


    private static void generateReport(List<MoneyTransfer> moneyTransfers, List<String> errorMessages) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(REPORT_FILE_NAME))) {
            writer.println("Отчет:");
            writer.println();

            writer.println("Успешно обработанные операции:");
            for (MoneyTransfer moneyTransfer : moneyTransfers) {
                writer.println(new Date() + " - " + moneyTransfer.getFromAccount() + " -> " +
                        moneyTransfer.getToAccount() + " : " + moneyTransfer.getAmount());

            }

            if (!errorMessages.isEmpty()) {
                writer.println();
                writer.println("Необработанные файлы:");
                for (String errorMessage : errorMessages) {
                    writer.println(errorMessage);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при записи в файл отчета", e);
        }
    }


    private static void logResult(String fileName, String result) {
        System.out.println(new Date() + " - " + fileName + " - " + result);
    }

    private static void printAllOperations(List<MoneyTransfer> moneyTransfers) {
        if (moneyTransfers.isEmpty()) {
            System.out.println("Нет операций для вывода.");
        } else {
            System.out.println("Все операции:");
            for (MoneyTransfer moneyTransfer : moneyTransfers) {
                System.out.println(moneyTransfer.getFromAccount() + " -> " +
                        moneyTransfer.getToAccount() + " : " + moneyTransfer.getAmount());
            }
        }
    }
}