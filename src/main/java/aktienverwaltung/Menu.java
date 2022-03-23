package aktienverwaltung;

import java.awt.Color;
import java.awt.Component;
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
import com.google.gson.JsonSyntaxException;

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
    boolean isDarkmode = false;

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

        //menuGui();
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
            panel.add(ipField);
    
            JLabel portLabel = new JLabel("Port:");
            portLabel.setBounds(380, 170, 150, 20);
            portLabel.setForeground(textColor);
            panel.add(portLabel);
    
            JTextField portField = new JTextField(4);
            portField.setBounds(450, 170, 50, 20);
            panel.add(portField);
    
            JButton weiter = new JButton("Weiter");
            weiter.setBounds(430, 225, 95, 25);
            weiter.setBackground(Color.WHITE);
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

                String[] serverMsg = { "getusers" };
                sendMessage(serverMsg);
        
                try {
                    accounts = gson.fromJson(bufferedReader.readLine(), Account[].class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        
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
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        panel.setBackground(bgColor);

        frame.add(panel);
        frame.setVisible(true);
    }

    private void startMenu() {
        JButton login = new JButton("Anmelden");
        login.setBackground(Color.WHITE);
        login.setBounds(420, 150, 125, 25);

        JButton register = new JButton("Registrieren");
        register.setBackground(Color.WHITE);
        register.setBounds(420, 190, 125, 25);

        JButton settings = new JButton("Einstellungen");
        settings.setBackground(Color.WHITE);
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
                for (Component com : panel.getComponents()) {
                    panel.remove(com);
                }

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
        ipField.setText(ip);
        panel.add(ipField);

        JLabel portLabel = new JLabel("Port:");
        portLabel.setBounds(380, 170, 150, 20);
        portLabel.setForeground(textColor);
        panel.add(portLabel);

        JTextField portField = new JTextField(4);
        portField.setBounds(450, 170, 50, 20);
        portField.setText(String.valueOf(port));
        panel.add(portField);

        JLabel darkMode = new JLabel("Dark Mode:");
        darkMode.setForeground(textColor);
        darkMode.setBounds(380, 190, 150, 20);

        JCheckBox darkModeBox = new JCheckBox();
        darkModeBox.setForeground(Color.WHITE);
        darkModeBox.setBounds(450, 190, 20, 20);

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
                    textColor = Color.WHITE;

                    panel.setBackground(Color.DARK_GRAY);
                    isDarkmode = true;
                }
                else {
                    bgColor = Color.WHITE;
                    textColor = Color.BLACK;

                    panel.setBackground(Color.WHITE);
                    isDarkmode = false;
                }

                for (Component com : panel.getComponents()) {
                    panel.remove(com);
                }

                panel.updateUI();

                startMenu();
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

        JButton weiter = new JButton("Weiter");
        weiter.setBackground(Color.WHITE);

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

                    register2(emailStr, password);
                }
            }
        });

        panel.add(email);
        panel.add(emailInput);
        panel.add(passwd);
        panel.add(passwdInput);
        panel.add(passwdRepeat);
        panel.add(passwdRepeatInput);
        panel.add(weiter);

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

        JButton weiter = new JButton("Weiter");
        weiter.setBackground(Color.WHITE);
        weiter.setBounds(400, 230, 100, 25);

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

                    Account newAccount = new Account(fNameStr, lNameStr, email, hashPassword(passwd), birthdateStr, phonenumberStr);
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

        panel.add(firstName);
        panel.add(fNameInput);
        panel.add(lastName);
        panel.add(lNameInput);
        panel.add(birthdate);
        panel.add(birthdateInput);
        panel.add(phonenumber);
        panel.add(phonenumberInput);
        panel.add(weiter);

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

        JButton weiter = new JButton("Weiter");
        weiter.setBackground(Color.WHITE);
        weiter.setBounds(425, 220, 100, 25);

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

                        account = acc;

                        menuGui();
                    }
                }
            }
        });

        panel.add(email);
        panel.add(emailInput);
        panel.add(passwd);
        panel.add(passwdInput);
        panel.add(weiter);

        panel.updateUI();
    }

    private void menuGui() {
        JButton konto = new JButton("Konto");
        konto.setBackground(Color.WHITE);

        JButton aktien = new JButton("Aktien");
        aktien.setBackground(Color.WHITE);

        JButton portfolio = new JButton("Portfolio");
        portfolio.setBackground(Color.WHITE);

        JButton bank = new JButton("Bank");
        bank.setBackground(Color.WHITE);

        panel.add(konto);
        panel.add(aktien);
        panel.add(portfolio);
        panel.add(bank);

        panel.updateUI();
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
            accounts = gson.fromJson(usersString, Account[].class);
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
        String tempmail;
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