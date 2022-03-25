package laba4;
import java.net.*;
import java.io.*;
import java.util.Properties;

public class Client {
    public static final int PORT = 8000;
    public static final String HOST = "localhost";
    public static int n = 5;
    public static int m = 5;
    private static boolean exit = false;
    
    public static void main(String[] args) throws IOException {
        // Потоки приёма, передачи
        try (Socket clientSocket = new Socket(HOST, PORT)) {
            // Потоки приёма, передачи
            BufferedReader console_reader = new BufferedReader(
                    new InputStreamReader(System.in));
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(clientSocket.getOutputStream()));
            
            while (!exit){
                String cj_filepath = "C:/Users/artur/Documents/NetBeansProjects/Laba4/ServerJournal.txt";
                File server_journal = new File(cj_filepath);
                
                // Выбор массива
                for(int i = 0; i <= 3; i++){
                    System.out.println(reader.readLine());
                }
                
                String type_of_array = choose_array(console_reader);
                send_massage(writer, type_of_array, server_journal);

                // Вывод массива
                for (int i = 0; i < n; i++){
                    System.out.println(reader.readLine());
                }
                System.out.println(reader.readLine());
                
                // Ввод ячейки массива
                int row = get_int(console_reader);
                int column = get_int(console_reader);
                

                // Проверка правильности ввода ячейки
                while ((row >= n) | (column >= m)){
                    System.out.println("Error! Try again");
                    row = get_int(console_reader);
                    column = get_int(console_reader);
                }

                // Передача строки ячейки
                send_massage(writer, String.valueOf(row), server_journal);

                // Передача столбца ячейки
                send_massage(writer, String.valueOf(column), server_journal);

                // Узнаём о защищенности ячейки
                boolean protect = Boolean.valueOf(reader.readLine());

                // Выбор дальнейших действий
                for(int i = 0; i <= 3; i++){
                    System.out.println(reader.readLine());
                }
                // Выбор
                exit = choose_action(console_reader);
                send_massage(writer, String.valueOf(exit), server_journal);

                if (protect){

                    } else {
                        if (exit){
                            System.out.println(reader.readLine());
                            // Получаем новое значение ячейки от клинта
                            // Передаём новое значение серверу
                            if ("1".equals(type_of_array)){
                                int new_value = get_int(console_reader);
                                send_massage(writer, String.valueOf(new_value), server_journal);
                            }
                            if ("2".equals(type_of_array)){
                                double new_value = get_double(console_reader);
                                send_massage(writer, String.valueOf(new_value), server_journal);
                            }
                            if ("3".equals(type_of_array)){
                                String new_value = console_reader.readLine();
                                send_massage(writer, new_value, server_journal);
                            }
                        }
                        for(int i = 0; i < 3; i++){
                                System.out.println(reader.readLine());
                            }
                        exit = choose_action(console_reader);
                        send_massage(writer, String.valueOf(exit), server_journal);
                    }
            }
            reader.close();
            writer.close();
            console_reader.close();
        } catch (SocketException e) {
            System.err.println("Исключение: " + e.toString());
        }
    }
    
    
    // Вспомогательные методы
    private static boolean choose_action(BufferedReader console_reader) throws IOException{
        String choise = console_reader.readLine();
        while (!(choise.equals("0") | choise.equals("1"))){
            System.out.println("Ошибка ввода, попробуйте ещё раз:");
            choise = console_reader.readLine();
        }
        exit = "1".equals(choise);
        return exit;
    }
    private static String choose_array(BufferedReader console_reader) throws IOException{
        String choise = console_reader.readLine();
        while (!(choise.equals("1") | choise.equals("2") | choise.equals("3"))){
            System.out.println("Ошибка ввода, попробуйте ещё раз:");
            choise = console_reader.readLine();
        }
        return choise;
    }
    private static void send_massage(BufferedWriter writer, String text, File server_journal) throws IOException{
        writer.write(text);
        writer.newLine();
        writer.flush();
        FileOutputStream(server_journal, text + "\n");
    }
    private static int get_int(BufferedReader reader) throws IOException{
        boolean error = true;
        int value = 0;
        // Получаем новое значение ячейки от клинта
        while (error){
            try{
                value = Integer.parseInt(reader.readLine());
                error = false;
            } catch (IOException | NumberFormatException e){
                System.out.println("Ошибка, введите значение ещё раз");
            }
        }
        return value;
    }
    private static double get_double(BufferedReader reader) throws IOException{
        boolean error = true;
        double value = 0;
        // Получаем новое значение ячейки от клинта
        while (error){
            try{
                value = Double.parseDouble(reader.readLine());
                error = false;
            } catch (IOException | NumberFormatException e){
                System.out.println("Ошибка, введите значение ещё раз");
            }
        }
        return value;
    }
    static String FileInputStream(String filepath){
        //C:\Users\artur\Documents\NetBeansProjects\Laba4\ClientJournal.txt
        File inputfile = new File(filepath);
        StringBuilder sb = new StringBuilder();
        
        if(inputfile.exists()){
            try{
                BufferedReader reader = new BufferedReader(new FileReader(inputfile.getAbsoluteFile()));
                try{
                    String s;
                    while((s = reader.readLine())!=null){//построчное чтение
                        sb.append(s);
                        sb.append("\n");
                    }
                }finally{reader.close();}
            }catch(IOException e){throw new RuntimeException();}
        }
        return sb.toString();
    }
    static void FileOutputStream(File outputfile, String text){
        String prev = FileInputStream(outputfile.getAbsolutePath());
        try {
            if(!outputfile.exists()) outputfile.createNewFile();
            PrintWriter printer = new PrintWriter(outputfile.getAbsoluteFile());
            try{
                printer.println(prev+text);
                }finally{printer.close();}
            }catch(IOException e){throw new RuntimeException();}
    }

    Client(Socket clientSocket, Client[] clients) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
