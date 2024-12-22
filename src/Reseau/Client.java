package Reseau;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class Client {
    private static final int PORT = 5000;

    public Client() {
        setupGUI();
    }

    private void setupGUI() {
        // Frame setup
        JFrame frame = new JFrame("File Sharing Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        // Modern header design
        JLabel header = new JLabel("File Sharing Client", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 28));
        header.setForeground(Color.WHITE);
        header.setBackground(new Color(70, 130, 180));
        header.setOpaque(true);

        // Main panel with GroupLayout
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLUE);
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // Components
        JLabel serverCodeLabel = new JLabel("Server Code:");
        JTextField serverCodeField = new JTextField(20);

        JLabel fileLabel = new JLabel("Choose File:");
        JButton fileButton = new JButton("Browse");
        JLabel filePathLabel = new JLabel("No file selected", JLabel.CENTER);
        filePathLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        filePathLabel.setForeground(Color.GRAY);

        JTextField commentField = new JTextField("Add a comment...", 20);
        JButton sendButton = new JButton("Send File");

        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);

        // Layout setup
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(header)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(serverCodeLabel)
                        .addComponent(serverCodeField))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(fileLabel)
                        .addComponent(fileButton)
                        .addComponent(filePathLabel))
                .addComponent(commentField)
                .addComponent(progressBar)
                .addComponent(sendButton)
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(header)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(serverCodeLabel)
                        .addComponent(serverCodeField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(fileLabel)
                        .addComponent(fileButton)
                        .addComponent(filePathLabel))
                .addComponent(commentField)
                .addComponent(progressBar)
                .addComponent(sendButton)
        );

        frame.add(panel);
        frame.setVisible(true);

        // File chooser action
        fileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnVal = fileChooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                filePathLabel.setText(selectedFile.getAbsolutePath());
            }
        });

        // Send file action
        sendButton.addActionListener(e -> {
            String serverCode = serverCodeField.getText();
            String filePath = filePathLabel.getText();
            String comment = commentField.getText();

            if (serverCode.isEmpty() || filePath.equals("No file selected")) {
                JOptionPane.showMessageDialog(null, "Server code and file must be provided!");
                return;
            }

            // Simulate progress bar
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    progressBar.setIndeterminate(true);
                    sendFile(serverCode, filePath, comment);
                    return null;
                }

                @Override
                protected void done() {
                    progressBar.setIndeterminate(false);
                }
            };
            worker.execute();
        });
    }

    private void sendFile(String serverCode, String filePath, String comment) {
        try (Socket socket = new Socket("localhost", PORT);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             FileInputStream fis = new FileInputStream(filePath)) {

            File file = new File(filePath);
            dos.writeUTF(file.getName());
            dos.writeUTF(comment);
            dos.writeLong(file.length());

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) > 0) {
                dos.write(buffer, 0, bytesRead);
            }

            JOptionPane.showMessageDialog(null, "File sent successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::new);
    }
}
