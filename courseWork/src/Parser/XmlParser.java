package Parser;

import Model.MoneyTransfer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XmlParser {
    public static List<MoneyTransfer> parseXmlFile(File file) {
        List<MoneyTransfer> moneyTransfers = new ArrayList<>();

        try (FileReader reader = new FileReader(file)) {
            StringBuilder xmlContent = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                xmlContent.append((char) c);
            }

            String xmlString = xmlContent.toString();
            MoneyTransfer moneyTransfer = parseXmlString(xmlString);

            moneyTransfers.add(moneyTransfer);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении файла XML", e);
        }

        return moneyTransfers;
    }

    private static MoneyTransfer parseXmlString(String xmlString) {
        MoneyTransfer moneyTransfer = new MoneyTransfer();

        // Простая реализация парсера для приведенного примера
        String[] tokens = xmlString.split("[<>]");
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].trim();

            if (!token.isEmpty()) {
                switch (token) {
                    case "fromAccount":
                        String fromAccount = tokens[i + 1];
                        if (isValidAccount(fromAccount)) {
                            moneyTransfer.setFromAccount(fromAccount);
                        } else {
                            throw new RuntimeException("Некорректный номер счета: " + fromAccount);
                        }
                        break;
                    case "toAccount":
                        String toAccount = tokens[i + 1];
                        if (isValidAccount(toAccount)) {
                            moneyTransfer.setToAccount(toAccount);
                        } else {
                            throw new RuntimeException("Некорректный номер счета: " + toAccount);
                        }
                        break;
                    case "amount":
                        int amount = Integer.parseInt(tokens[i + 1]);
                        if (amount > 0) {
                            moneyTransfer.setAmount(amount);
                        } else {
                            throw new RuntimeException("Некорректное значение суммы: " + amount);
                        }
                        break;
                }
            }
        }

        return moneyTransfer;
    }

    private static boolean isValidAccount(String account) {
        return account.length() == 10;
    }
}