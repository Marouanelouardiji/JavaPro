//package Reseau;
//
//
//import java.io.*;
//import java.net.*;
//import java.sql.*;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//public class Database {
//    public class Server {
//        private static final int PORT = 5000;
//        private static final String DB_URL = "jdbc:sqlite:files.db";
//
//        public Server() {
//            setupDatabase();
//            startServer();
//        }
//
//        private void setupDatabase() {
//            try (Connection connection = DriverManager.getConnection(DB_URL);
//                 Statement statement = connection.createStatement()) {
//
//                // Create the files table if it doesn't exist
//                String createTableSQL = """
//                CREATE TABLE IF NOT EXISTS files (
//                    id INTEGER PRIMARY KEY AUTOINCREMENT,
//                    name TEXT NOT NULL,
//                    size INTEGER NOT NULL,
//                    comment TEXT,
//                    upload_time TEXT NOT NULL
//                )
//            """;
//                statement.execute(createTableSQL);
//                System.out.println("Database setup complete.");
//            } catch (SQLException e) {
//                System.err.println("Database setup error: " + e.getMessage());
//            }
//        }
//
//        private void startServer() {
//            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
//                System.out.println("Server is running on port " + PORT);
//
//                while (true) {
//                    Socket clientSocket = serverSocket.accept();
//                    System.out.println("Client connected: " + clientSocket.getInetAddress());
//                    handleClient(clientSocket);
//                }
//            } catch (IOException e) {
//                System.err.println("Server error: " + e.getMessage());
//            }
//        }
//
//        private void handleClient(Socket clientSocket) {
//            new Thread(() -> {
//                try (DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
//                     FileOutputStream fos = new FileOutputStream("server_files/" + dis.readUTF())) {
//
//                    // Receive file metadata
//                    String fileName = dis.readUTF();
//                    String comment = dis.readUTF();
//                    long fileSize = dis.readLong();
//
//                    // Save the file
//                    byte[] buffer = new byte[4096];
//                    int bytesRead;
//                    while ((bytesRead = dis.read(buffer)) > 0) {
//                        fos.write(buffer, 0, bytesRead);
//                        fileSize -= bytesRead;
//                        if (fileSize <= 0) break;
//                    }
//                    System.out.println("File received: " + fileName);
//
//                    // Insert metadata into the database
//                    saveFileMetadata(fileName, comment, fileSize);
//
//                    clientSocket.close();
//                    System.out.println("Client disconnected.");
//                } catch (IOException e) {
//                    System.err.println("Error handling client: " + e.getMessage());
//                }
//            }).start();
//        }
//
//        private void saveFileMetadata(String fileName, String comment, long fileSize) {
//            String insertSQL = "INSERT INTO files (name, size, comment, upload_time) VALUES (?, ?, ?, ?)";
//            try (Connection connection = DriverManager.getConnection(DB_URL);
//                 PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
//
//                preparedStatement.setString(1, fileName);
//                preparedStatement.setLong(2, fileSize);
//                preparedStatement.setString(3, comment);
//                preparedStatement.setString(4, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//
//                preparedStatement.executeUpdate();
//                System.out.println("File metadata saved to database.");
//            } catch (SQLException e) {
//                System.err.println("Error saving file metadata: " + e.getMessage());
//            }
//        }
//
//        public void main(String[] args) {
//            new Server();
//        }
//    }
//}
