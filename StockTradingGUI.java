import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

class Stock {
        String symbol;
        double price;

        public Stock(String symbol, double price) {
                this.symbol = symbol;
                this.price = price;
        }
}

class Transaction {
        String type, symbol;
        int quantity;
        double price;

        public Transaction(String type, String symbol, int quantity, double price) {
                this.type = type;
                this.symbol = symbol;
                this.quantity = quantity;
                this.price = price;
        }

        public String toString() {
                return type + " | " + symbol + " | Qty: " + quantity + " | ₹" + price;
        }
}

class User {
        double balance = 10000;

        HashMap<String, Integer> portfolio = new HashMap<>();
        ArrayList<Transaction> history = new ArrayList<>();

        public boolean buy(String symbol, int qty, double price) {
                double cost = qty * price;

                if (cost > balance)
                        return false;

                balance -= cost;
                portfolio.put(symbol,
                                portfolio.getOrDefault(symbol, 0) + qty);

                history.add(new Transaction("BUY", symbol, qty, price));
                return true;
        }

        public boolean sell(String symbol, int qty, double price) {

                if (!portfolio.containsKey(symbol)
                                || portfolio.get(symbol) < qty)
                        return false;

                balance += qty * price;

                portfolio.put(symbol,
                                portfolio.get(symbol) - qty);

                if (portfolio.get(symbol) == 0)
                        portfolio.remove(symbol);

                history.add(new Transaction("SELL", symbol, qty, price));

                return true;
        }
}

public class StockTradingGUI extends JFrame {

        private User user = new User();

        private Stock[] stocks = {
                        new Stock("AAPL", 150),
                        new Stock("TSLA", 250),
                        new Stock("GOOG", 180),
                        new Stock("MSFT", 210)
        };

        private JLabel balanceLabel;

        private JTextArea portfolioArea;
        private JTextArea historyArea;

        private JTable marketTable;

        public StockTradingGUI() {

                setTitle("Stock Trading Platform");
                setSize(800, 600);
                setDefaultCloseOperation(EXIT_ON_CLOSE);
                setLocationRelativeTo(null);

                setLayout(new BorderLayout());

                balanceLabel = new JLabel(
                                "Balance: ₹" + user.balance);

                balanceLabel.setFont(
                                new Font("Arial", Font.BOLD, 18));

                add(balanceLabel, BorderLayout.NORTH);

                // Market Table
                String[] columns = { "Stock", "Price" };

                DefaultTableModel model = new DefaultTableModel(columns, 0);

                for (Stock s : stocks) {
                        model.addRow(
                                        new Object[] {
                                                        s.symbol,
                                                        s.price
                                        });
                }

                marketTable = new JTable(model);

                add(new JScrollPane(marketTable),
                                BorderLayout.CENTER);

                JPanel buttonPanel = new JPanel();

                JButton buyBtn = new JButton("Buy");
                JButton sellBtn = new JButton("Sell");

                buttonPanel.add(buyBtn);
                buttonPanel.add(sellBtn);

                add(buttonPanel, BorderLayout.SOUTH);

                JTabbedPane tabs = new JTabbedPane();

                portfolioArea = new JTextArea();
                historyArea = new JTextArea();

                tabs.add("Portfolio",
                                new JScrollPane(portfolioArea));

                tabs.add("History",
                                new JScrollPane(historyArea));

                add(tabs, BorderLayout.EAST);

                buyBtn.addActionListener(e -> buyStock());

                sellBtn.addActionListener(e -> sellStock());

                refreshDisplay();

                setVisible(true);
        }

        private void buyStock() {

                int row = marketTable.getSelectedRow();

                if (row == -1) {
                        JOptionPane.showMessageDialog(
                                        this,
                                        "Select a stock first!");
                        return;
                }

                String qtyStr = JOptionPane.showInputDialog(
                                this,
                                "Enter Quantity:");

                if (qtyStr == null)
                        return;

                int qty = Integer.parseInt(qtyStr);

                Stock stock = stocks[row];

                if (user.buy(stock.symbol,
                                qty,
                                stock.price)) {

                        JOptionPane.showMessageDialog(
                                        this,
                                        "Purchase Successful!");
                } else {
                        JOptionPane.showMessageDialog(
                                        this,
                                        "Insufficient Balance!");
                }

                refreshDisplay();
        }

        private void sellStock() {

                int row = marketTable.getSelectedRow();

                if (row == -1) {
                        JOptionPane.showMessageDialog(
                                        this,
                                        "Select a stock first!");
                        return;
                }

                String qtyStr = JOptionPane.showInputDialog(
                                this,
                                "Enter Quantity:");

                if (qtyStr == null)
                        return;

                int qty = Integer.parseInt(qtyStr);

                Stock stock = stocks[row];

                if (user.sell(stock.symbol,
                                qty,
                                stock.price)) {

                        JOptionPane.showMessageDialog(
                                        this,
                                        "Sale Successful!");
                } else {
                        JOptionPane.showMessageDialog(
                                        this,
                                        "Not enough shares!");
                }

                refreshDisplay();
        }

        private void refreshDisplay() {

                balanceLabel.setText(
                                "Balance: ₹" +
                                                String.format("%.2f",
                                                                user.balance));

                StringBuilder portfolioText = new StringBuilder();

                for (String stock : user.portfolio.keySet()) {

                        portfolioText.append(stock)
                                        .append(" : ")
                                        .append(user.portfolio.get(stock))
                                        .append(" shares\n");
                }

                portfolioArea.setText(
                                portfolioText.toString());

                StringBuilder historyText = new StringBuilder();

                for (Transaction t : user.history) {

                        historyText.append(t)
                                        .append("\n");
                }

                historyArea.setText(
                                historyText.toString());
        }

        public static void main(String[] args) {

                SwingUtilities.invokeLater(
                                StockTradingGUI::new);
        }
}