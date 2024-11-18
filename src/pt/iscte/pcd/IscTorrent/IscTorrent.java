package pt.iscte.pcd.IscTorrent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class IscTorrent {
    private static IscTorrent INSTANCE;
    private JFrame frame;
    private Node node;
    DefaultListModel<String> resultsList;

    private IscTorrent(String path, int port) {
        frame = new JFrame("IscTorrent [path=" + path + ", ip=localhost: " + port + "]");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(dimension.width / 2, dimension.height / 2);
        frame.setLocationRelativeTo(null);
        addFrameContent();

        node = new Node(path, port);
        node.runServer();
    }

    public static IscTorrent getInstance(String path, int port) {
        if (INSTANCE == null) {
            INSTANCE = new IscTorrent(path, port);
        }
        return INSTANCE;
    }

    public static IscTorrent getInstance() {
        return INSTANCE;
    }

    public Node getNode() {
        return node;
    }

    public void addFrameContent() {
        frame.setLayout(new BorderLayout());

        JLabel searchText = new JLabel("Texto a procurar:");
        JTextField searchField = new JTextField(30);
        JButton searchButton = new JButton("Procurar");
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(searchText);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        resultsList = new DefaultListModel<>();
        JList<String> list = new JList<>(resultsList);
        JScrollPane listPane = new JScrollPane(list);

        JButton downloadButton = new JButton("Descarregar");
        JButton makeConnection = new JButton("Ligar a Nó");
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 1));
        buttonsPanel.add(downloadButton);
        buttonsPanel.add(makeConnection);

        frame.add(searchPanel, BorderLayout.NORTH);
        frame.add(listPane, BorderLayout.CENTER);
        frame.add(buttonsPanel, BorderLayout.EAST);

        searchButton.addActionListener((ActionEvent e) -> searchFiles(searchField.getText()));
        makeConnection.addActionListener((ActionEvent e) -> connectToNodeDialog());
        downloadButton.addActionListener((ActionEvent e) -> downloadSelectedFile(list.getSelectedValue()));

        frame.setVisible(true);
    }

    public void searchFiles(String search) {
        if (search == null || search.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Por favor insira um texto válido para procurar.");
            return;
        }
        WordSearchMessage message = new WordSearchMessage(search, node.getPort());
        node.sendMessage(message);
    }

    public void connectToNodeDialog() {
        JTextField ipField = new JTextField("localhost");
        JTextField portField = new JTextField();
        Object[] message = {
                "Endereço:", ipField,
                "Porta:", portField
        };
        int option = JOptionPane.showConfirmDialog(null, message,
                "Adicionar Nó", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String ip = ipField.getText();
            String port = portField.getText();
            try {
                node.connectToNode(ip, Integer.parseInt(port));
                JOptionPane.showMessageDialog(frame, "Ligado ao endereço: " + ip + " Porta: " + port);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Erro ao ligar ao nó.");
            }
        }
    }

    public void downloadSelectedFile(String file) {
        if (file != null) {
            // TODO download file
            // TODO time taken and who provided the file
            JOptionPane.showMessageDialog(frame,
                    "");
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Por favor selecione um ficheiro para descarregar.");
        }
    }

    public void removeConnection(int port) {
        node.removeConnection(port);
        JOptionPane.showMessageDialog(frame, "Ligação ao nó " + port + " removida.");
    }

    public static void main(String[] args) {
        @SuppressWarnings("unused")
        IscTorrent iscTorrent = getInstance(args[0], Integer.parseInt(args[1]));
    }
}
