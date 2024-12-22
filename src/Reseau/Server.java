package Reseau;

import javax.swing.*;
        import java.awt.*;
        import java.io.*;
        import java.net.*;

public class Server {
    private static final int PORT = 5000;
    private final String serverCode;

    public Server() {
        // Generate a unique server code
        serverCode = "01"; // Hardcoded to "01"
        setupGUI();
    }

    private void setupGUI() {
        // Frame setup
        JFrame frame = new JFrame("File Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null); // Center the window
        frame.setResizable(false);

        // Header Label
        JLabel header = new JLabel("File Sharing Server", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 24));
        header.setForeground(Color.WHITE);
        header.setBackground(new Color(0, 51, 102));
        header.setOpaque(true);

        // Server info panel with server code
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BorderLayout());
        logoPanel.setBackground(new Color(240, 240, 240));

        JLabel logoLabel = new JLabel(new ImageIcon("path/to/server-logo.png"));
        logoPanel.add(logoLabel, BorderLayout.CENTER);
        logoPanel.add(new JLabel("Server Code: " + serverCode, JLabel.CENTER), BorderLayout.SOUTH);

        // Log display area
        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Theme customization
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(Color.GREEN);

        // Arrange components in the frame
        frame.add(header, BorderLayout.NORTH);
        frame.add(logoPanel, BorderLayout.CENTER);
        frame.add(scrollPane, BorderLayout.SOUTH);
        frame.setVisible(true);

        // Start the server in a new thread
        new Thread(() -> startServer(logArea)).start();
    }

    private void startServer(JTextArea logArea) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logArea.append("Server started on port " + PORT + "\n");
            logArea.append("Waiting for clients to connect...\n");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logArea.append("Reseau.Client connected.\n");
                handleClient(clientSocket, logArea);
            }
        } catch (IOException e) {
            logArea.append("Error starting server: " + e.getMessage() + "\n");
        }
    }

    private void handleClient(Socket clientSocket, JTextArea logArea) {
        try (
                DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream())
        ) {
            // Receive file metadata
            String fileName = dis.readUTF();
            String comment = dis.readUTF();
            long fileSize = dis.readLong();

            logArea.append("Received file: " + fileName + " (" + fileSize + " bytes)\n");
            logArea.append("Comment: " + comment + "\n");

            // Ask for confirmation to save
            int choice = JOptionPane.showConfirmDialog(
                    null,
                    "Do you want to save the file: " + fileName + "?",
                    "File Received",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Choose directory to save the file");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int returnVal = fileChooser.showSaveDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File saveDir = fileChooser.getSelectedFile();
                    File receivedFile = new File(saveDir, fileName);

                    // Write file
                    try (FileOutputStream fos = new FileOutputStream(receivedFile)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        long remaining = fileSize;
                        while ((bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, remaining))) > 0) {
                            fos.write(buffer, 0, bytesRead);
                            remaining -= bytesRead;
                        }
                    }

                    logArea.append("File saved at: " + receivedFile.getAbsolutePath() + "\n");
                }
            } else {
                logArea.append("File rejected by the user.\n");
            }
        } catch (IOException e) {
            logArea.append("Error handling client: " + e.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Server::new);
    }
}
