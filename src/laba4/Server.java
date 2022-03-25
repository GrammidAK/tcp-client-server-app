package laba4;
import java.net.*;
import java.io.*;

public class Server {
    private static ServerSocket serverSocket;
    private static int PORT = 8000;
    private static final int COUNT_TO_SEND = 10;
    private static int count = 0;
    private static String cj_filepath = null;
    private static final int n = 5;
    private static final int m = 5;
    private static final int protected_row_1 = 0;
    private static final int protected_row_2 = 1;
    private static final int protected_column_1 = 0;
    private static final int protected_column_2 = 1;
    private static int[][] int_arr = new int[n][m];
    private static double[][] double_arr = new double[n][m];
    private static String[][] string_arr = new String[n][m];
    
    public static void main(String[] args) throws IOException, InterruptedException {
        Server tcpServer = new Server();
        if (args.length > 0){
            cj_filepath = args[0];
            System.out.println("file");
        } else {
            cj_filepath = "C:/Users/artur/Documents/NetBeansProjects/Laba4/ClientJournal.txt";
        }
        tcpServer.go();
    }
    
    public Server(){
       try {
            serverSocket = new ServerSocket(PORT, 10);
            System.out.println("Server");
            // Заполнение массивов
            for (int i = 0; i < n; i++){
                for (int j = 0; j < m; j++){
                    int_arr[i][j] = i+j;
                    double_arr[i][j] = (i+j)*1.1;
                    string_arr[i][j] = "a";
                }
            }
        } catch (IOException e){
            System.err.println("Не удаётся открыть сокет для сервера: " + e.toString());
        }
    }
        
    public void go() throws IOException{
        class Listener implements Runnable{
            Socket clientSocket;
            public Listener(Socket aSocket) throws IOException{
                clientSocket = aSocket;
                this.reader = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                this.writer = new BufferedWriter(
                        new OutputStreamWriter(clientSocket.getOutputStream()));
            }

            File client_journal = new File(cj_filepath);
            // Потоки приёма, передачи
            BufferedWriter writer;
            BufferedReader reader;

            public void run(){
                try{
                    System.out.println("Client accepted " + (++count));
                    client_communication(writer, reader, client_journal, int_arr, double_arr, string_arr);
                    writer.close();
                    reader.close();
                    clientSocket.close();
                }catch(IOException e){
                  System.err.println("Исключение: " + e.toString());
                }
            }

        }
        while(true){
            try{
                Socket clientSocket = serverSocket.accept();
                Listener listener = new Listener(clientSocket);
                Thread thread = new Thread(listener);
                thread.start();
            } catch(IOException e){
                System.err.println("Исключение: " + e.toString());
            }
        }
    }
    
    public void client_communication(BufferedWriter writer, BufferedReader reader, File client_journal, int[][] int_arr, double[][] double_arr, String[][] string_arr) throws IOException{
        boolean exit = false;
        while (!exit){
            // Выбор массива
            String text = "Choose array:\r\n"
                    + "1 - Integer\r\n"
                    + "2 - Real\r\n"
                    + "3 - String";
            send_massage(writer, text, client_journal);
            String type_of_array = reader.readLine();

            StringBuilder sb = new StringBuilder();
            // Массив int
            if ("1".equals(type_of_array)){
                for (int i = 0; i < n; i++){
                    for (int j = 0; j < m; j++){
                        sb.append(int_arr[i][j]).append(" ");
                    }
                    if (i < n-1){
                        sb.append("\n");
                    }
                }
                send_massage(writer, sb.toString(), client_journal);
            }
            if ("2".equals(type_of_array)){
                for (int i = 0; i < n; i++){
                    for (int j = 0; j < m; j++){
                        sb.append(double_arr[i][j]).append(" ");
                    }
                    if (i < n-1){
                        sb.append("\n");
                    }
                }
                send_massage(writer, sb.toString(), client_journal);
            }
            if ("3".equals(type_of_array)){
                for (int i = 0; i < n; i++){
                    for (int j = 0; j < m; j++){
                        sb.append(string_arr[i][j]).append(" ");
                    }
                    if (i < n-1){
                        sb.append("\n");
                    }
                }
                send_massage(writer, sb.toString(), client_journal);
            }

            text = "Choose a cell";
            send_massage(writer, text, client_journal);

            // Получаем ячейку массива
            int row = Integer.parseInt(reader.readLine());
            int column = Integer.parseInt(reader.readLine());

            // Проверяем защищённость
            boolean protect = true;
            if ((row >= protected_row_1)&(row <= protected_row_2)&(column >= protected_column_1)&(column <= protected_column_2)){
            } else{
                protect = false;
            }
            // Передаём данные о защищенности ячейки
            send_massage(writer, String.valueOf(protect), client_journal);

            // Передаём значение ячейки клиенту
            if ("1".equals(type_of_array)){
                send_massage(writer, "Значение ячейки = " + String.valueOf(int_arr[row][column]), client_journal);
            }
            if ("2".equals(type_of_array)){
                send_massage(writer, "Значение ячейки = " + String.valueOf(double_arr[row][column]), client_journal);
            }
            if ("3".equals(type_of_array)){
                send_massage(writer, "Значение ячейки = " + string_arr[row][column], client_journal);
            }

            if (protect){
                text = "Вы не можете изменить эту ячейку\r\n"
                        + "0 - Продолжить\r\n"
                        + "1 - Выйти";
            } else{
                text = "Хотите изменить значение?\r\n"
                        + "0 - Нет\r\n"
                        + "1 - Да";
            }
            send_massage(writer, text, client_journal);

            // Получаем выбор клиента
            exit = Boolean.valueOf(reader.readLine());

            if (!protect){
                if (exit){
                    text = "Введите новое значение ячейки:";
                    send_massage(writer, text, client_journal);
                    // Получаем новое значение от клиента
                    if ("1".equals(type_of_array)){
                        int new_value = Integer.parseInt(reader.readLine());
                        int_arr[row][column] = new_value;
                    }
                    if ("2".equals(type_of_array)){
                        double new_value = Double.parseDouble(reader.readLine());
                        double_arr[row][column] = new_value;
                    }
                    if ("3".equals(type_of_array)){
                        String new_value = reader.readLine();
                        string_arr[row][column] = new_value;
                    }
                }
                text = "Выберите дальнейшее действие:\r\n"
                    + "0 - Продолжить\r\n"
                    + "1 - Выйти";
                send_massage(writer, text, client_journal);

                exit = Boolean.valueOf(reader.readLine());
            }
        }
    }
    // Вспомогательные методы
    private static void send_massage(BufferedWriter writer, String text, File client_journal) throws IOException{
        writer.write(text);
        writer.newLine();
        writer.flush();
        FileOutputStream(client_journal, text + "\n");
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
}
