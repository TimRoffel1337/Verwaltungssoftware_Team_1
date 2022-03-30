package aktienverwaltung;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Menu {
    Gson gson;

    JFrame frame = new JFrame();
    JPanel panel = new JPanel();

    Socket socket = null;
    InputStreamReader inputStreamReader = null;
    OutputStreamWriter outPutStreamWriter = null;
    BufferedReader bufferedReader = null;
    BufferedWriter bufferedWriter = null;

    Color bgColor = Color.WHITE;
    Color textColor = Color.BLACK;
    Color fieldColor = Color.WHITE;
    Color buttonColor = Color.WHITE;

    boolean isDarkmode = false;

    Stock[] stocks;
    Account[] accounts;
    Account account;

    private String ip;
    private short port = 443;

    public Menu() {
        gson = new Gson();

        panel.setLayout(null);

        //ENTFERNEN
        frame.setSize(960, 540);
        frame.setTitle("Aktienverwaltungs Programm");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(panel);
        frame.setVisible(true);

        //stockMenu();
        gui();
        //start();
    }

    public void gui() {
        String dir = "C:/Users/" + System.getProperty("user.name") + "/Documents/Aktienverwaltung/";
        File ipFile = new File(dir + "ip.json");
        File portFile = new File(dir + "port.json");

        if (!ipFile.exists()) {
            JLabel ipLabel = new JLabel("Server IP:");
            ipLabel.setBounds(380, 150, 150, 20);
            ipLabel.setForeground(textColor);
            panel.add(ipLabel);
    
            JTextField ipField = new JTextField(10);
            ipField.setBounds(450, 150, 100, 20);
            ipField.setBackground(fieldColor);
            panel.add(ipField);
    
            JLabel portLabel = new JLabel("Port:");
            portLabel.setBounds(380, 170, 150, 20);
            portLabel.setForeground(textColor);
            panel.add(portLabel);
    
            JTextField portField = new JTextField(4);
            portField.setBounds(450, 170, 50, 20);
            portField.setBackground(fieldColor);
            panel.add(portField);
    
            JButton weiter = new JButton("Weiter");
            weiter.setBounds(430, 225, 95, 25);
            weiter.setBackground(buttonColor);
            panel.add(weiter);

            panel.updateUI();
    
            weiter.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ip = ipField.getText();
                    port = Short.valueOf(portField.getText());
    
                    JLabel errorMsg = new JLabel("Es konnte keine Verbindung mit dem Server aufgebaut werden");
                    errorMsg.setForeground(Color.RED);
                    errorMsg.setLocation(200, 200);
    
                    if (connectToServer()) {
                        panel.remove(portField);
                        panel.remove(ipField);
                        panel.remove(weiter);
                        panel.remove(ipLabel);
                        panel.remove(portLabel);
    
                        if (panel.isAncestorOf(errorMsg)) {
                            panel.remove(errorMsg);
                        }

                        saveIpandPort(ipFile, portFile);
    
                        panel.updateUI();
                        startMenu();
                    }
                    else {
                        panel.updateUI();
                    }
                }
            });
        }
        else {
            ip = "127.0.0.1";
            port = 443;

            try {
                BufferedReader ipReader = new BufferedReader(new FileReader(ipFile));
                ip = ipReader.readLine();
                ipReader.close();

                BufferedReader portReader = new BufferedReader(new FileReader(portFile));
                port = Short.valueOf(portReader.readLine());
                portReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (connectToServer()) {
                System.out.println("Gettings all user");

                getAllUser();
        
                System.out.println("Successfully got all user");

                startMenu();
            }
            else {
                JLabel errorMsg = new JLabel("Es konnte keine Verbindung zum Server hergestellt werden");
                errorMsg.setForeground(Color.RED);

                panel.add(errorMsg);
            }
        }

        frame.setSize(960, 540);
        frame.setTitle("Aktienverwaltungs Programm");

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.out.println("Closing");
                try {
                    String[] msg = { "updateuser", gson.toJson(account)};
                    sendMessage(msg);

                    outPutStreamWriter.close();
                    inputStreamReader.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        panel.setBackground(bgColor);

        frame.add(panel);
        frame.setVisible(true);
    }

    private void startMenu() {
        JButton login = new JButton("Anmelden");
        login.setBackground(buttonColor);
        login.setBounds(420, 150, 125, 25);

        JButton register = new JButton("Registrieren");
        register.setBackground(buttonColor);
        register.setBounds(420, 190, 125, 25);

        JButton settings = new JButton("Einstellungen");
        settings.setBackground(buttonColor);
        settings.setBounds(25, 450, 125, 25);

        register.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.remove(register);
                panel.remove(login);

                registerMenu();
            }
        });

        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.remove(register);
                panel.remove(login);

                loginMenu();
            }
        });

        settings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.removeAll();

                settingsMenu();
            }
        });

        panel.add(login);
        panel.add(register);
        panel.add(settings);

        panel.updateUI();
    }

    private void settingsMenu() {
        JLabel ipLabel = new JLabel("Server IP:");
        ipLabel.setBounds(380, 150, 150, 20);
        ipLabel.setForeground(textColor);
        panel.add(ipLabel);

        JTextField ipField = new JTextField(10);
        ipField.setBounds(450, 150, 100, 20);
        ipField.setBackground(fieldColor);
        ipField.setText(ip);
        panel.add(ipField);

        JLabel portLabel = new JLabel("Port:");
        portLabel.setBounds(380, 170, 150, 20);
        portLabel.setForeground(textColor);
        panel.add(portLabel);

        JTextField portField = new JTextField(4);
        portField.setBounds(450, 170, 50, 20);
        portField.setText(String.valueOf(port));
        portField.setBackground(fieldColor);
        panel.add(portField);

        JLabel darkMode = new JLabel("Dark Mode:");
        darkMode.setForeground(textColor);
        darkMode.setBounds(380, 190, 150, 20);

        JCheckBox darkModeBox = new JCheckBox();
        darkModeBox.setForeground(Color.WHITE);
        darkModeBox.setBounds(450, 190, 20, 20);
        darkModeBox.setSelected(isDarkmode);

        JButton save = new JButton("Speichern");
        save.setBounds(420, 220, 100, 20);

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ip = ipField.getText();
                port = Short.valueOf(portField.getText());

                String dir = "C:/Users/" + System.getProperty("user.name") + "/Documents/Aktienverwaltung/";
                File ipFile = new File(dir + "ip.json");
                File portFile = new File(dir + "port.json");

                saveIpandPort(ipFile, portFile);

                if (darkModeBox.isSelected()) {
                    bgColor = Color.DARK_GRAY;
                    buttonColor = Color.lightGray;
                    textColor = Color.WHITE;
                    fieldColor = Color.lightGray;

                    panel.setBackground(Color.DARK_GRAY);
                    isDarkmode = true;

                    panel.removeAll();

                    if (account != null) {
                        account.setDarkmode(true);
                        panel.updateUI();
                        menuGui();
                    }
                    else {
                        panel.updateUI();
                        startMenu();
                    }
                }
                else {
                    bgColor = Color.WHITE;
                    textColor = Color.BLACK;
                    buttonColor = Color.WHITE;
                    fieldColor = Color.WHITE;

                    panel.setBackground(Color.WHITE);
                    isDarkmode = false;

                    panel.removeAll();

                    if (account != null) {
                        account.setDarkmode(false);
                        panel.updateUI();
                        menuGui();
                    }
                    else {
                        panel.updateUI();
                        startMenu();
                    }
                }
            }
        });

        panel.add(ipLabel);
        panel.add(portLabel);
        panel.add(portField);
        panel.add(ipField);
        panel.add(darkMode);
        panel.add(darkModeBox);
        panel.add(save);

        panel.updateUI();
    }

    private void registerMenu() {
        JLabel email = new JLabel("E-mail:");
        JLabel passwd = new JLabel("Passwort:");
        JLabel passwdRepeat = new JLabel("Passwort wiederholen:");

        email.setBounds(300, 150, 100, 25);
        passwd.setBounds(300, 175, 100, 25);
        passwdRepeat.setBounds(300, 200, 150, 25);

        email.setForeground(textColor);
        passwd.setForeground(textColor);
        passwdRepeat.setForeground(textColor);

        JTextField emailInput = new JTextField(10);
        JPasswordField passwdInput = new JPasswordField(10);
        JPasswordField passwdRepeatInput = new JPasswordField(10);

        emailInput.setBounds(450, 150, 150, 25);
        passwdInput.setBounds(450, 175, 150, 25);
        passwdRepeatInput.setBounds(450, 200, 150, 25);

        emailInput.setBackground(fieldColor);
        passwdInput.setBackground(fieldColor);
        passwdRepeatInput.setBackground(fieldColor);

        JButton weiter = new JButton("Weiter");
        weiter.setBackground(buttonColor);

        JButton back = new JButton("Zurück");
        back.setBackground(buttonColor);
        back.setBounds(800, 450, 100, 25);

        weiter.setBounds(400, 250, 100, 25);
        weiter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String emailStr = emailInput.getText();
                String password = String.valueOf(passwdInput.getPassword());
                String passwordRepeat = String.valueOf(passwdRepeatInput.getPassword());

                if (password.equals(passwordRepeat) && isEmailValid(emailStr)) {
                    panel.remove(email);
                    panel.remove(passwd);
                    panel.remove(passwdRepeat);
                    panel.remove(emailInput);
                    panel.remove(passwdInput);
                    panel.remove(passwdRepeatInput);
                    panel.remove(weiter);
                    panel.remove(back);

                    register2(emailStr, password);
                }
            }
        });

        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.removeAll();

                startMenu();
            }
        });

        panel.add(email);
        panel.add(emailInput);
        panel.add(passwd);
        panel.add(passwdInput);
        panel.add(passwdRepeat);
        panel.add(passwdRepeatInput);
        panel.add(weiter);
        panel.add(back);

        panel.updateUI();
    }

    private void register2(String email, String passwd) {
        JLabel firstName = new JLabel("Vorname:");
        JLabel lastName = new JLabel("Nachname:");
        JLabel birthdate = new JLabel("Geburtsdatum:");
        JLabel phonenumber = new JLabel("Handynummer:");

        firstName.setBounds(350, 100, 100, 25);
        lastName.setBounds(350, 130, 100, 25);
        birthdate.setBounds(350, 160, 100, 25);
        phonenumber.setBounds(350, 190, 100, 25);

        firstName.setForeground(textColor);
        lastName.setForeground(textColor);
        birthdate.setForeground(textColor);
        phonenumber.setForeground(textColor);

        JTextField fNameInput = new JTextField(10);
        JTextField lNameInput = new JTextField(10);
        JTextField birthdateInput = new JTextField(10);
        JTextField phonenumberInput = new JTextField(10);

        fNameInput.setBounds(450, 100, 120, 25);
        lNameInput.setBounds(450, 130, 120, 25);
        birthdateInput.setBounds(450, 160, 120, 25);
        phonenumberInput.setBounds(450, 190, 120, 25);

        fNameInput.setBackground(fieldColor);
        lNameInput.setBackground(fieldColor);
        birthdateInput.setBackground(fieldColor);
        phonenumberInput.setBackground(fieldColor);

        JButton weiter = new JButton("Weiter");
        weiter.setBackground(buttonColor);
        weiter.setBounds(400, 230, 100, 25);

        JButton back = new JButton("Zurück");
        back.setBackground(buttonColor);
        back.setBounds(800, 450, 100, 25);

        weiter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fNameStr = fNameInput.getText();
                String lNameStr = lNameInput.getText();
                String birthdateStr = birthdateInput.getText();
                String phonenumberStr = phonenumberInput.getText();

                if ((fNameStr != null && lNameStr != null && birthdateStr != null && phonenumberStr != null) && isValidBirthdate(birthdateStr)) {
                    panel.remove(firstName);
                    panel.remove(lastName);
                    panel.remove(birthdate);
                    panel.remove(phonenumber);
                    panel.remove(fNameInput);
                    panel.remove(lNameInput);
                    panel.remove(birthdateInput);
                    panel.remove(phonenumberInput);
                    panel.remove(weiter);
                    panel.remove(back);

                    Portfolio portfolio = new Portfolio(1000);
                    Account newAccount = new Account(fNameStr, lNameStr, email, hashPassword(passwd), birthdateStr, phonenumberStr, portfolio);
                    String[] msg = { "adduser", gson.toJson(newAccount) };
                    sendMessage(msg);

                    Account[] newAccounts = new Account[accounts.length + 1];
                    for (int i = 0; i < accounts.length - 1; i++) {
                        newAccounts[i] = accounts[i];
                    }
                    newAccounts[accounts.length] = newAccount;
                    accounts = newAccounts;

                    startMenu();
                }
            }
        });

        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.removeAll();

                startMenu();
            }
        });

        panel.add(firstName);
        panel.add(fNameInput);
        panel.add(lastName);
        panel.add(lNameInput);
        panel.add(birthdate);
        panel.add(birthdateInput);
        panel.add(phonenumber);
        panel.add(phonenumberInput);
        panel.add(weiter);
        panel.add(back);

        panel.updateUI();
    }

    private void loginMenu() {
        JLabel email = new JLabel("Email:");
        JLabel passwd = new JLabel("Passwort:");

        email.setBounds(400, 150, 100, 25);
        passwd.setBounds(400, 180, 100, 25);

        email.setForeground(textColor);
        passwd.setForeground(textColor);

        JTextField emailInput = new JTextField(10);
        JPasswordField passwdInput = new JPasswordField(10);

        emailInput.setBounds(475, 150, 150, 25);
        passwdInput.setBounds(475, 180, 150, 25);

        emailInput.setBackground(fieldColor);
        passwdInput.setBackground(fieldColor);

        JButton weiter = new JButton("Weiter");
        weiter.setBackground(buttonColor);
        weiter.setBounds(425, 220, 100, 25);

        JButton back = new JButton("Zurück");
        back.setBackground(buttonColor);
        back.setBounds(800, 450, 100, 25);

        weiter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Account acc : accounts) {
                    if (acc.getEmail().equals(emailInput.getText()) && acc.getHashedPassword().equals(hashPassword(String.valueOf(passwdInput.getPassword())))) {
                        panel.remove(email);
                        panel.remove(emailInput);
                        panel.remove(passwd);
                        panel.remove(passwdInput);
                        panel.remove(weiter);
                        panel.remove(back);

                        account = acc;
                        isDarkmode = account.getDarkmode();

                        if (isDarkmode == true) {
                            bgColor = Color.DARK_GRAY;
                            buttonColor = Color.lightGray;
                            textColor = Color.WHITE;
                            fieldColor = Color.lightGray;
        
                            panel.setBackground(Color.DARK_GRAY);
                        }
                        else {
                            bgColor = Color.WHITE;
                            textColor = Color.BLACK;
                            buttonColor = Color.WHITE;
                            fieldColor = Color.WHITE;
        
                            panel.setBackground(Color.WHITE);
                        }

                        panel.updateUI();
                        menuGui();
                    }
                }
            }
        });

        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.removeAll();

                startMenu();
            }
        });

        panel.add(email);
        panel.add(emailInput);
        panel.add(passwd);
        panel.add(passwdInput);
        panel.add(weiter);
        panel.add(back);

        panel.updateUI();
    }

    private void menuGui() {
        JButton konto = new JButton("Konto");
        konto.setBackground(buttonColor);
        konto.setBounds(425, 100, 125, 35);

        JButton aktien = new JButton("Aktien");
        aktien.setBackground(buttonColor);
        aktien.setBounds(425, 145, 125, 35);

        JButton portfolio = new JButton("Portfolio");
        portfolio.setBackground(buttonColor);
        portfolio.setBounds(425, 190, 125, 35);

        JButton bank = new JButton("Bank");
        bank.setBackground(buttonColor);
        bank.setBounds(425, 235, 125, 35);

        JButton settings = new JButton("Einstellungen");
        settings.setBackground(buttonColor);
        settings.setBounds(25, 450, 125, 25);


        //add a button to logout
        JButton logout = new JButton("Abmelden");
        logout.setBackground(buttonColor);
        logout.setBounds(800, 450, 100, 25);

        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.removeAll();

                String[] msg = { "updateuser", gson.toJson(account) };
                sendMessage(msg);

                account = null;
                stocks = null;

                System.out.println("Logging out");

                getAllUser();
                startMenu();
            }
        });

        settings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.removeAll();

                settingsMenu();
            }
        });

        aktien.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.remove(konto);
                panel.remove(aktien);
                panel.remove(portfolio);
                panel.remove(bank);
                panel.remove(settings);
                panel.remove(logout);

                stockMenu();
            }
        });

        portfolio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.remove(konto);
                panel.remove(aktien);
                panel.remove(portfolio);
                panel.remove(bank);
                panel.remove(logout);

                portfolioMenu();
            }
        });

        panel.add(konto);
        panel.add(aktien);
        panel.add(portfolio);
        panel.add(bank);
        panel.add(settings);
        panel.add(logout);

        panel.updateUI();
    }

    private void stockMenu() {
        getStocks();

        JButton back = new JButton("Zurück");
        back.setBackground(buttonColor);
        back.setBounds(800, 450, 100, 25);

        JButton settings = new JButton("Einstellungen");
        settings.setBackground(buttonColor);
        settings.setBounds(25, 450, 125, 25);

        settings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.removeAll();

                settingsMenu();
            }
        });

        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.removeAll();

                menuGui();
            }
        });

        for (int i = 0; i < stocks.length; i++) {
            JButton button = new JButton(stocks[i].getName());
            button.setBackground(buttonColor);
            button.setBounds(25, 100 + (i * 35), 125, 35);

            Stock st = stocks[i];

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    panel.removeAll();

                    stockInfo(st);
                }
            });

            panel.add(button);
        }

        panel.add(back);
        panel.add(settings);

        panel.updateUI();
    }

    private void portfolioMenu() {
        JButton back = new JButton("Zurück");
        back.setBackground(buttonColor);
        back.setBounds(800, 450, 100, 25);

        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.removeAll();

                menuGui();
            }
        });

        JLabel money = new JLabel("Portfolio Geld: " + account.getPortfolio().getMoney());
        money.setForeground(textColor);
        money.setBounds(25, 100, 200, 35);

        float pStocksValue = 0;
        int stockAmount = 0;

        if (account.getPortfolio().getStocks() != null) {
            for (Stock stock : account.getPortfolio().getStocks()) {
                pStocksValue += stock.getCurrentPrice();
                stockAmount++;
            }
        }

        JLabel stocksValue = new JLabel("Portfolio Aktien Wert: " + pStocksValue);
        stocksValue.setForeground(textColor);
        stocksValue.setBounds(25, 145, 200, 35);

        JLabel stockLen = new JLabel("Aktien Anzahl im Portfolio: " + stockAmount);
        stockLen.setForeground(textColor);
        stockLen.setBounds(25, 190, 200, 35);

        String stockNamesList = "";
        if (account.getPortfolio().getStocks() != null) {
            for (int i = 0; i < account.getPortfolio().getStocks().length; i++) {
                stockNamesList += account.getPortfolio().getStocks()[i].getName();
                if (i != account.getPortfolio().getStocks().length - 1) {
                    stockNamesList += ", ";
                }
            }
        }
        else {
            stockNamesList = "Keine Aktien im Portfolio";
        }

        JLabel stockNames = new JLabel("Aktien Namen: " + stockNamesList);
        stockNames.setForeground(textColor);
        stockNames.setBounds(25, 235, 250, 35);

        panel.updateUI();

        panel.add(back);
        panel.add(stocksValue);
        panel.add(stockLen);
        panel.add(stockNames);
    }

    private void stockInfo(Stock stock) {
        //list the min, max, average and current price of the stock
        JLabel name = new JLabel("Name: " + stock.getName());
        JLabel min = new JLabel("Min: " + stock.getMin() + "€");
        JLabel max = new JLabel("Max: " + stock.getMax() + "€");
        JLabel average = new JLabel("Durchschnitt: " + stock.getAverage() + "€");
        JLabel current = new JLabel("Aktueller Preis: " + stock.getCurrentPrice() + "€");

        name.setBounds(400, 100, 200, 25);
        min.setBounds(400, 125, 200, 25);
        max.setBounds(400, 150, 200, 25);
        average.setBounds(400, 175, 200, 25);
        current.setBounds(400, 200, 200, 25);

        name.setForeground(textColor);
        min.setForeground(textColor);
        max.setForeground(textColor);
        average.setForeground(textColor);
        current.setForeground(textColor);

        JButton back = new JButton("Zurück");
        back.setBackground(buttonColor);
        back.setBounds(800, 450, 100, 25);

        JButton buy = new JButton("Kaufen");
        buy.setBackground(Color.GREEN);
        buy.setBounds(400, 250, 125, 35);

        JButton sell = new JButton("Verkaufen");
        sell.setBackground(Color.RED);
        sell.setBounds(400, 295, 125, 35);

        buy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.remove(buy);
                panel.remove(sell);

                JLabel label = new JLabel("Wie viele Aktien möchtest du kaufen?");
                label.setForeground(textColor);
                label.setBounds(360, 250, 250, 25);

                JTextField input = new JTextField();
                input.setBounds(435, 285, 50, 25);

                JButton kaufen = new JButton("Kaufen");
                kaufen.setBackground(Color.GREEN);
                kaufen.setBounds(400, 325, 125, 35);

                JLabel notEnough = new JLabel("Du hast nicht genug Geld!");
                notEnough.setForeground(Color.RED);
                notEnough.setBounds(400, 425, 200, 25);
                notEnough.setVisible(false);

                JLabel isValidNumber = new JLabel("Bitte gib eine gültige Anzahl an!");
                isValidNumber.setForeground(Color.RED);
                isValidNumber.setBounds(385, 425, 200, 25);
                isValidNumber.setVisible(false);

                kaufen.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String inputStr = input.getText();

                        if (notEnough.isVisible()) {
                            notEnough.setVisible(false);
                        }
                        if (isValidNumber.isValid()) {
                            isValidNumber.setVisible(false);
                        }

                        int inputInt = 0;
                        try {
                            inputInt = Integer.parseInt(inputStr);

                            if (inputInt > 0 && account.getPortfolio().getMoney() >= inputInt * stock.getCurrentPrice()) {
                                account.getPortfolio().removeMoney(inputInt * stock.getCurrentPrice());
                                account.getPortfolio().addStock(stock, inputInt);
                            } else {
                                if (!(account.getPortfolio().getMoney() >= inputInt * stock.getCurrentPrice())) {
                                    notEnough.setVisible(true);
                                    panel.updateUI();   
                                }
                                else if (!(inputInt > 0)) {
                                    isValidNumber.setVisible(true);
                                    panel.updateUI();
                                }
                            }
                        } catch (NumberFormatException ex) {
                            System.out.println("Invalid input");
                        }
                    }
                });

                JButton abbrechen = new JButton("Abbrechen");
                abbrechen.setBackground(Color.RED);
                abbrechen.setBounds(400, 370, 125, 35);

                abbrechen.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        panel.remove(label);
                        panel.remove(input);
                        panel.remove(kaufen);
                        panel.remove(abbrechen);
                        panel.remove(notEnough);
                        panel.remove(isValidNumber);

                        panel.add(buy);
                        panel.add(sell);
                        panel.updateUI();
                    }
                });

                panel.add(isValidNumber);
                panel.add(notEnough);
                panel.add(label);
                panel.add(input);
                panel.add(kaufen);
                panel.add(abbrechen);

                panel.updateUI();
            }
        });

        sell.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.removeAll();

                stockMenu();
            }
        });

        JButton settings = new JButton("Einstellungen");
        settings.setBackground(buttonColor);
        settings.setBounds(25, 450, 125, 25);

        settings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.removeAll();

                settingsMenu();
            }
        });


        panel.add(name);
        panel.add(min);
        panel.add(max);
        panel.add(average);
        panel.add(current);
        panel.add(settings);
        panel.add(back);
        panel.add(buy);
        panel.add(sell);

        panel.updateUI();
    }

    private void getStocks() {
        String[] msg = { "getstocks" };
        sendMessage(msg);

        System.out.println("getting all stocks");

        String response = "";
        try {
            response = bufferedReader.readLine();
            Stock[] stocks = gson.fromJson(response, Stock[].class);
            this.stocks = stocks;
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Successfully got all stocks");
    }

    public void start() {
        String dir = "C:/Users/" + System.getProperty("user.name") + "/Documents/Aktienverwaltung/";
        File ipPath = new File(dir);
        File ipFile = new File(dir + "ip.json");

        try {
            Scanner scanner = new Scanner(System.in);

            if (!ipFile.exists()) {
                ipPath.mkdirs();
                ipFile.createNewFile();
                System.out.println("Gebe bitte die Server IP ein: ");
                ip = scanner.nextLine();

                BufferedWriter writer = new BufferedWriter(new FileWriter(ipFile));;
                writer.write(gson.toJson(ip));
                writer.close();
            }
            else {
                bufferedReader = new BufferedReader(new FileReader(ipFile));
                ip = gson.fromJson(bufferedReader.readLine(), String.class);
            }

            connectToServer();

            socket = new Socket(ip, 443);
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            outPutStreamWriter = new OutputStreamWriter(socket.getOutputStream());

            bufferedReader = new BufferedReader(inputStreamReader);
            bufferedWriter = new BufferedWriter(outPutStreamWriter);

            getAllUser();

            while(true) {
                String[] in = scanner.nextLine().split(" ");

                if (in[0].equalsIgnoreCase("login") && in.length > 2) {
                    sendMessage(in);

                    System.out.println(bufferedReader.readLine());
                }
                else if (in[0].equalsIgnoreCase("register")) {
                    register(scanner);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (outPutStreamWriter != null) {
                    outPutStreamWriter.close();
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean connectToServer() {
        try {
            socket = new Socket(ip, port);
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            outPutStreamWriter = new OutputStreamWriter(socket.getOutputStream());
    
            bufferedReader = new BufferedReader(inputStreamReader);
            bufferedWriter = new BufferedWriter(outPutStreamWriter);

            return true;
        } catch(IOException e) {
            System.out.println("Failed to connect to the Server!");
        }

        return false;
    }

    Person[] getAllUser() {
        String[] msg = { "getusers" };
        sendMessage(msg);

        String usersString;
        try {
            usersString = bufferedReader.readLine();
            String[] accountsStr = gson.fromJson(usersString, String[].class);

            Account[] users = new Account[accountsStr.length];
            for (int i = 0; i < accountsStr.length; i++) {
                users[i] = gson.fromJson(accountsStr[i], Account.class);
            }

            this.accounts = users;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void register(Scanner scanner) {
        /*bufferedReader = new BufferedReader(inputStreamReader);
        String userName = "";
        String password;
        String birth;
        String phonenumber;

        //Email
        boolean hasChoosen = false;
        while (hasChoosen == false) {
            userName = isemailvalid(scanner);
            boolean isAvailable = true;

            for (Account account : accounts) {
                if (account.getUsername().equals(userName)) {
                    isAvailable = false;
                    break;
                }
            }
            if (isAvailable == true) {
                hasChoosen = true;
                break;
            }
            else if (isAvailable == false) {
                System.out.println("Benutzername bereits vergeben. Bitte gebe einen neuen ein:");
            }
        }

        //passwort
        System.out.println("Legen Sie ihr Passwort fest (mindestens 8 Zeichen lang):");
        String temppassword;
        temppassword = scanner.next();
        while (temppassword.length() < 8) {
            System.out.println("Das Passwort muss mindestens 8 Zeichen lang sein");
            temppassword = scanner.next();
        }
        password = hashPassword(temppassword);

        //Geburtsdatum
        birth = birthdate(scanner);

        //Telefonnummer
        System.out.println("Geben sie ihre Telefonnummer an:");
        phonenumber = scanner.nextLine();
        
        //senden
        String[] msg = { userName, password, birth, phonenumber };
        sendMessage(msg);*/
    }
    
    //Überprüft auf dd.MM.yyyy Format
    private boolean isValidBirthdate(String birthdate){
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        
        format.setLenient(false);

        try {
            format.parse(birthdate);
            //wenn die Eingabe keinene Fehler hervorruft, geht der Code weiter

            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    //nimmt einen String und gibt ihn als hash zurück
    private String hashPassword(String password){
        StringBuilder sb = new StringBuilder();

        try {
            MessageDigest md = MessageDigest.getInstance("SHA3-256");
            byte[]hashInBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));

            for (byte b : hashInBytes) {
                sb.append(String.format("%02x", b));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        return sb.toString();
    }

    //email regex
    private boolean isEmailValid(String email) {
        String regex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
        boolean matchFound = false;

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        matchFound = matcher.find();

        if (matchFound == true) {
            return true;
        } else {
            return false;
        }
    }

    private void saveIpandPort(File ipFile, File portFile) {
        try {
            BufferedWriter ipWriter = new BufferedWriter(new FileWriter(ipFile));
            ipWriter.write(ip);
            ipWriter.close();
    
            BufferedWriter portWriter = new BufferedWriter(new FileWriter(portFile));
            portWriter.write(String.valueOf(port));
            portWriter.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String[] msg) {
        try {
            bufferedWriter.write(gson.toJson(msg));
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}