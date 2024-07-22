import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class AESGUIII {
    private static JTextArea encryptInputTextArea;
    private static JTextArea encryptOutputTextArea;
    private static JTextArea decryptInputTextArea;
    private static JTextArea decryptOutputTextArea;

    private static JTextField keyTextField;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the main frame
        JFrame frame = new JFrame("AES Encryption/Decryption System");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.WHITE);

        // Create the title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("AES Encryption/Decryption System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel);
        titlePanel.setBackground(new Color(0, 102, 204)); // Blue background
        titleLabel.setForeground(Color.WHITE); // White text color

        // Create encryption panel
        JPanel encryptPanel = new JPanel();
        encryptPanel.setLayout(new GridLayout(1, 2));

        JPanel encryptInputPanel = new JPanel();
        encryptInputPanel.setLayout(new BorderLayout());
        encryptInputPanel.add(new JLabel("Enter Text to Encrypt:"), BorderLayout.NORTH);
        encryptInputTextArea = new JTextArea(10, 30);
        encryptInputTextArea.setBackground(new Color(255, 255, 204)); // Light yellow background
        encryptInputPanel.add(new JScrollPane(encryptInputTextArea), BorderLayout.CENTER);

        JPanel encryptButtonPanel = new JPanel();
        JButton encryptButton = new JButton("Encrypt");
        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processText(true, encryptInputTextArea, encryptOutputTextArea, keyTextField);
            }
        });
        encryptButton.setBackground(new Color(0, 102, 0)); // Dark green background
        encryptButton.setForeground(Color.WHITE); // White text color
        encryptButtonPanel.add(encryptButton);

        JPanel encryptOutputPanel = new JPanel();
        encryptOutputPanel.setLayout(new BorderLayout());
        encryptOutputPanel.add(new JLabel("Encrypted Text:"), BorderLayout.NORTH);
        encryptOutputTextArea = new JTextArea(10, 30);
        encryptOutputTextArea.setBackground(new Color(204, 255, 204)); // Light green background
        encryptOutputPanel.add(new JScrollPane(encryptOutputTextArea), BorderLayout.CENTER);

        // Add encryption components to the encryption panel
        encryptInputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        encryptButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        encryptOutputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        encryptPanel.add(encryptInputPanel);
        encryptPanel.add(encryptButtonPanel);
        encryptPanel.add(encryptOutputPanel);

        // Create decryption panel
        JPanel decryptPanel = new JPanel();
        decryptPanel.setLayout(new GridLayout(1, 2));

        JPanel decryptInputPanel = new JPanel();
        decryptInputPanel.setLayout(new BorderLayout());
        decryptInputPanel.add(new JLabel("Enter Text to Decrypt:"), BorderLayout.NORTH);
        decryptInputTextArea = new JTextArea(10, 30);
        decryptInputTextArea.setBackground(new Color(255, 255, 204)); // Light yellow background
        decryptInputPanel.add(new JScrollPane(decryptInputTextArea), BorderLayout.CENTER);

        JPanel decryptButtonPanel = new JPanel();
        JButton decryptButton = new JButton("Decrypt");
        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processText(false, decryptInputTextArea, decryptOutputTextArea, keyTextField);
            }
        });
        decryptButton.setBackground(new Color(204, 0, 0)); // Dark red background
        decryptButton.setForeground(Color.WHITE); // White text color
        decryptButtonPanel.add(decryptButton);

        JPanel decryptOutputPanel = new JPanel();
        decryptOutputPanel.setLayout(new BorderLayout());
        decryptOutputPanel.add(new JLabel("Decrypted Text:"), BorderLayout.NORTH);
        decryptOutputTextArea = new JTextArea(10, 30);
        decryptOutputTextArea.setBackground(new Color(255, 204, 204)); // Light pink background
        decryptOutputPanel.add(new JScrollPane(decryptOutputTextArea), BorderLayout.CENTER);

        // Add decryption components to the decryption panel
        decryptInputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        decryptButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        decryptOutputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        decryptPanel.add(decryptInputPanel);
        decryptPanel.add(decryptButtonPanel);
        decryptPanel.add(decryptOutputPanel);

        // Create key panel
        JPanel keyPanel = new JPanel();
        keyPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel keyLabel = new JLabel("Key:");
        keyTextField = new JTextField(20);
        keyTextField.setBackground(new Color(255, 255, 204)); // Light yellow background
        JButton newKeyButton = new JButton("New Key");
        newKeyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateNewKey();
            }
        });
        keyPanel.add(keyLabel);
        keyPanel.add(keyTextField);
        keyPanel.add(newKeyButton);

        // Add panels to the main frame
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(encryptPanel);
        mainPanel.add(decryptPanel);
        mainPanel.add(keyPanel);

        frame.add(titlePanel, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);

        frame.setVisible(true);

        // Generate initial key
        generateNewKey();
    }

    private static void processText(boolean encrypt, JTextArea inputTextArea, JTextArea outputTextArea, JTextField keyTextField) {
        try {
            // Get input text
            String inputText = inputTextArea.getText();

            // Get the encryption/decryption key
            String keyText = keyTextField.getText();
            byte[] keyBytes = keyText.getBytes(StandardCharsets.UTF_8);
            SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

            // Process the input
            String result;
            if (encrypt) {
                // Encrypt the input
                result = encryptAES(inputText, secretKey);
            } else {
                // Decrypt the input
                result = decryptAES(inputText, secretKey);
            }

            // Display the result
            outputTextArea.setText(result);

        } catch (Exception e) {
            e.printStackTrace();
            String operation = encrypt ? "encryption" : "decryption";
            JOptionPane.showMessageDialog(null, "Error during " + operation + ".", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String encryptAES(String plaintext, SecretKey secretKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String decryptAES(String encryptedText, SecretKey secretKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return "Decryption Error";
        }
    }

    private static void generateNewKey() {
        // Generate a new random key
        byte[] keyBytes = new byte[16];
        new SecureRandom().nextBytes(keyBytes);
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

        // Update the key text field
        keyTextField.setText(Base64.getEncoder().encodeToString(secretKey.getEncoded()));
    }
}
