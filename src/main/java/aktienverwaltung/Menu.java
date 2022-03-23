package aktienverwaltung;

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
import java.text.SimpleDateFormat;
import java.text.ParseException;
import com.google.gson.Gson;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Menu {
    Gson gson;

    Socket socket = null;
    InputStreamReader inputStreamReader = null;
    OutputStreamWriter outPutStreamWriter = null;
    BufferedReader bufferedReader = null;
    BufferedWriter bufferedWriter = null;

    Account[] accounts;

    public Menu() {
        start();
    }

    public void start() {
        String ip;
        String dir = "C:/Users/" + System.getProperty("user.name") + "/Documents/Aktienverwaltung/";
        File ipPath = new File(dir);
        File ipFile = new File(dir + "ip.json");

        try {
            gson = new Gson();
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
        bufferedReader = new BufferedReader(inputStreamReader);
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
        sendMessage(msg);
    }
    
    //Überprüft auf dd.MM.yyyy Format
    private String birthdate(Scanner sc){
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        
        format.setLenient(false);

        while(true){
            try {
                String tempdate;
                System.out.println("Geben sie ihr Geburtsdatum an");
                tempdate = sc.nextLine(); 
                format.parse(tempdate);
                //wenn die Eingabe keinene Fehler hervorruft, geht der Code weiter

                return tempdate;
            } catch (ParseException e) {
                System.out.println("Eingabe ist ungültig. Bittet im " + format.toPattern() + " Format" );
                continue;
            }
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
    private String isemailvalid(Scanner sc) {
        String tempmail;
        String regex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
        boolean matchFound = false;

        while (true) {
            tempmail = sc.next();
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(tempmail);

            matchFound = matcher.find();

            if (matchFound == true) {
                System.out.println("Match found");
                break;
            } else {
                System.out.println("Das ist keine gültige email Adresse");
                continue;
            }

        }
        return tempmail;
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