package aktienverwaltung;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class Menu {
    Gson gson;

    JFrame frame = new JFrame();
    JPanel panel = new JPanel();

    Socket socket = null;
    InputStreamReader inputStreamReader = null;
    OutputStreamWriter outPutStreamWriter = null;
    BufferedReader bufferedReader = null;
    BufferedWriter bufferedWriter = null;

    Account[] accounts;

    public Menu() {
        gson = new Gson();

        gui();
        //start();
    }

    public void gui() {
        String dir = "C:/Users/" + System.getProperty("user.name") + "/Documents/Aktienverwaltung/";
        File ipPath = new File(dir);
        File ipFile = new File(dir + "ip.json");
        File portFile = new File(dir + "port.json");

        if (!ipFile.exists()) {
            JLabel ipLabel = new JLabel("Server IP:");
            ipLabel.setLocation(0, 0);
            panel.add(ipLabel);
    
            JTextField ipField = new JTextField(10);
            ipField.setLocation(20, 50);
            panel.add(ipField);
    
            JLabel portLabel = new JLabel("Port:");
            portLabel.setLocation(0, 100);
            panel.add(portLabel);
    
            JTextField portField = new JTextField(4);
            portField.setLocation(50, 100);
            panel.add(portField);
    
            JButton weiter = new JButton("Weiter");
            weiter.setBounds(50,100,95,30);
            weiter.setBackground(Color.WHITE);
            panel.add(weiter);
    
            weiter.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String ip = ipField.getText();
                    short port = Short.valueOf(portField.getText());
    
                    JLabel errorMsg = new JLabel("Es konnte keine Verbindung mit dem Server aufgebaut werden");
                    errorMsg.setForeground(Color.RED);
                    errorMsg.setLocation(200, 200);
    
                    if (connectToServer(ip, port)) {
                        panel.remove(portField);
                        panel.remove(ipField);
                        panel.remove(weiter);
                        panel.remove(ipLabel);
                        panel.remove(portLabel);
    
                        if (panel.isAncestorOf(errorMsg)) {
                            panel.remove(errorMsg);
                        }

                        try {
                            BufferedWriter ipWriter = new BufferedWriter(new FileWriter(ipFile));
                            ipWriter.write(ip);
                            ipWriter.close();

                            BufferedWriter portWriter = new BufferedWriter(new FileWriter(portFile));
                            portWriter.write(String.valueOf(port));
                            portWriter.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
    
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
            String ip = "127.0.0.1";
            short port = 443;

            try {
                BufferedReader reader = new BufferedReader(new FileReader(ipFile));
                ip = reader.readLine();

                reader = new BufferedReader(new FileReader(portFile));
                port = Short.valueOf(reader.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (connectToServer(ip, port)) {
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

        frame.add(panel);
        frame.setVisible(true);
    }

    private void startMenu() {
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

        JButton login = new JButton("Anmelden");
        login.setBackground(Color.WHITE);

        JButton register = new JButton("Registrieren");
        register.setBackground(Color.WHITE);
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

        panel.add(login);
        panel.add(register);

        panel.updateUI();
    }

    private void registerMenu() {
        JLabel email = new JLabel("E-mail:");
        JLabel passwd = new JLabel("Passwort:");
        JLabel passwdRepeat = new JLabel("Passwort wiederholen:");

        JTextField emailInput = new JTextField(10);
        JPasswordField passwdInput = new JPasswordField(10);
        JPasswordField passwdRepeatInput = new JPasswordField(10);

        JButton weiter = new JButton("Weiter");
        weiter.setBackground(Color.WHITE);
        weiter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String emailStr = emailInput.getText();
                String password = passwdInput.getText();
                String passwordRepeat = passwdRepeatInput.getText();

                if (password.equals(passwordRepeat) && emailStr.contains("@")) {
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
        JLabel birtdate = new JLabel("Geburtsdatum:");
        JLabel phonenumber = new JLabel("Handynummer:");

        JTextField fNameInput = new JTextField(10);
        JTextField lNameInput = new JTextField(10);
        JTextField birtdateInput = new JTextField(10);
        JTextField phonenumberInput = new JTextField(10);


        panel.add(firstName);
        panel.add(fNameInput);
        panel.add(lastName);
        panel.add(lNameInput);
        panel.add(birtdate);
        panel.add(birtdateInput);
        panel.add(phonenumber);
        panel.add(phonenumberInput);

        panel.updateUI();
    }

    private void loginMenu() {
        JLabel username = new JLabel("Benutzername:");
        JLabel passwd = new JLabel("Passwort:");

        JTextField usernameInput = new JTextField(10);
        JTextField passwdInput = new JTextField(10);

        JButton weiter = new JButton("Weiter");
        weiter.setBackground(Color.WHITE);

        panel.add(username);
        panel.add(usernameInput);
        panel.add(passwd);
        panel.add(passwdInput);
        panel.add(weiter);

        panel.updateUI();
    }

    public void start() {
        String ip;
        short port = 443;
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

            connectToServer(ip, port);

            while(true) {
                String[] in = scanner.nextLine().split(" ");

                if (in[0].equalsIgnoreCase("login") && in.length > 2) {
                    sendMessage(in);

                    System.out.println(bufferedReader.readLine());
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

    private boolean connectToServer(String ip, short port) {
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