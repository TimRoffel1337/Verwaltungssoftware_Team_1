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

public class Menu {
    Gson gson;

    Socket socket = null;
    InputStreamReader inputStreamReader = null;
    OutputStreamWriter outPutStreamWriter = null;
    BufferedReader bufferedReader = null;
    BufferedWriter bufferedWriter = null;

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

            socket = new Socket(ip, 1234);
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            outPutStreamWriter = new OutputStreamWriter(socket.getOutputStream());

            bufferedReader = new BufferedReader(inputStreamReader);
            bufferedWriter = new BufferedWriter(outPutStreamWriter);

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

    public void register(Scanner scanner) {
        String userName;
        String password;
        String birth;
        String email;
        String phonenumber;

        System.out.println("Legen Sie ihren Benutzernamen fest:");
        userName = scanner.nextLine();
        
        System.out.println("Legen Sie ihr Passwort fest:");
        password = hash(scanner.nextLine());
        System.out.println(password);

        birth = birthdate();

        System.out.println("Geben sie ihre e-mail an:");
        email = scanner.nextLine();

        System.out.println("Geben sie ihre Telefonnummer an:");
        phonenumber = scanner.nextLine();
        
        //senden
        String[] msg = { userName, password, birth, email, phonenumber };
        sendMessage(msg);
    }
    
    //generiert ein Passwort nach dd.MM.yyyy Format
    private String birthdate(){
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        
        format.setLenient(false);

        while(true){
            try {
                String tempdate;
                Scanner sc = new Scanner(System.in);

                System.out.println("Geben sie ihr Geburtsdatum an");
                tempdate = sc.nextLine(); 
                format.parse(tempdate);
                //wenn die Eingabe keinene Fehler hervorruft, geht der Code weiter
                sc.close();
                return tempdate;
            } catch (ParseException e) {
                System.out.println("Eingabe ist ungültig. Bittet im " + format.toPattern() + " Format" );
                continue;
            }
        }    
    }

    //nimmt einen String und gibt ihn als hash zurück
    private String hash(String quelle){
        StringBuilder sb = new StringBuilder();

        try {
            MessageDigest md = MessageDigest.getInstance("SHA3-256");

            byte[]hashInBytes = md.digest(quelle.getBytes(StandardCharsets.UTF_8));

            for (byte b : hashInBytes) {
                sb.append(String.format("%02x", b));
            }

            
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        return sb.toString();
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