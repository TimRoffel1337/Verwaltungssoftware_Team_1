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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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

                    panel.updateUI();
                    startMenu();
                }
                else {
                    panel.updateUI();
                }
            }
        });

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

        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });

        panel.add(login);
        panel.add(register);

        panel.updateUI();
    }

    private void loginMenu() {

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