import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class SecureNoteManager extends JFrame {
    private JTextArea textArea;
    private JPasswordField passwordField;
    private JButton saveButton;

    public SecureNoteManager() {
        setTitle("Secure Notes");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // center window on screen

        // The main text area for note text
        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        // The password field
        passwordField = new JPasswordField(20);
        passwordField.setEchoChar((char) 0);  // visible charactrs

        // Buttons for saving and opening encrypted notes
        saveButton = new JButton("Save Encrypted Note");
        JButton openButton = new JButton("Open Encrypted Note");

        // Action listeners for each button
        saveButton.addActionListener(e -> saveEncryptedNote());
        openButton.addActionListener(e -> openEncryptedNote());

        // The bottom panel has the password input and buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(new JLabel("Password: "));
        bottomPanel.add(passwordField);
        bottomPanel.add(saveButton);
        bottomPanel.add(openButton);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void saveEncryptedNote() {
        try {
            String password = new String(passwordField.getPassword());
            String text = textArea.getText();

            if (password.isEmpty() || text.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password/note can't be empty before saving.");
                return;
            }

            // Checks to see if password has already been used in any existing file
            File currentDir = new File(".");
            File[] files = currentDir.listFiles((dir, name) -> name.matches("encrypted_note\\d+\\.txt"));
            if (files != null) {
                for (File file : files) {
                    try {
                        byte[] data = new byte[(int) file.length()];
                        try (FileInputStream fis = new FileInputStream(file)) {
                            fis.read(data);
                        }
                        String decrypted = Encryption.decrypt(data, password);
                        String[] lines = decrypted.split("\\n");
                        String lastLine = lines[lines.length - 1].trim();
                        
                        if (lastLine.equals("Password: " + password)) {
                            JOptionPane.showMessageDialog(this, "This passward is already used. Use a different one.");
                            return;
                        }
                    } catch (Exception e) {
                        // If there is no match, continue to next file
                    }
                }
            }

            //Embeds the password at the end of the note to allow verification after this session
            String contentToSave = text + "\nPassword: " + password;

            // Find a unique filename
            int fileIndex = 1;
            File file;
            do {
                file = new File("encrypted_note" + fileIndex + ".txt");
                fileIndex++;
            } while (file.exists());

            System.out.println("Saving encrypted note to: " + file.getAbsolutePath());

            // Encrypt then save the file
            byte[] encrypted = Encryption.encrypt(contentToSave, password);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(encrypted);
            }

            // Reset all the fields afterwards
            textArea.setText("");
            passwordField.setText("");
            JOptionPane.showMessageDialog(this, "Note was encrypted and saved as: " + file.getName());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save note.");
        }
    }

    private void openEncryptedNote() {
        try {
            String password = new String(passwordField.getPassword());

            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter the password to decrypt.");
                return;
            }

            // Get all encrypted note files
            File currentDir = new File(".");
            File[] files = currentDir.listFiles((dir, name) -> name.matches("encrypted_note\\d+\\.txt"));
            if (files == null || files.length == 0) {
                JOptionPane.showMessageDialog(this, "No encrypted notes found in project directory.");
                return;
            }

            boolean found = false;
            
            for (File file : files) {
                System.out.println("Trying to decrypt file: " + file.getAbsolutePath());
                try {
                    byte[] data = new byte[(int) file.length()];
                    try (FileInputStream fis = new FileInputStream(file)) {
                        fis.read(data);
                    }

                    String decrypted = Encryption.decrypt(data, password);
                    String[] lines = decrypted.split("\\n");
                    String lastLine = lines[lines.length - 1].trim();

                    // Check if password matches the one stored in the file
                    if (lastLine.equals("Password: " + password)) {
                    	
                        // Reconstruct note without the password line
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < lines.length - 1; i++) {
                            sb.append(lines[i]).append("\n");
                        }

                        // Shows decrypted note but lock it from edits
                        textArea.setText(sb.toString().trim());
                        textArea.setEditable(false);
                        saveButton.setEnabled(false);

                        JOptionPane.showMessageDialog(this, "Note decrypted from file: " + file.getName());
                        found = true;
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("decryption failed: " + file.getName());
                }
            }

            if (!found) {
                JOptionPane.showMessageDialog(this, "No matching encrypted note found for this password.");
            }

            passwordField.setText("");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while decrypting notes.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SecureNoteManager().setVisible(true));
    }
}
