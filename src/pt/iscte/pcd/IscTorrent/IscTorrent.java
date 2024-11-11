package pt.iscte.pcd.IscTorrent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class IscTorrent {
    private JFrame frame;
    private Node node;
    private List<Node> connectedNodes = new ArrayList<>();
    DefaultListModel<String> resultsList;

    public IscTorrent(String path, int port) {
        frame = new JFrame("IscTorrent [path=" + path + ", ip=localhost: " + port + "]");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension dimenson = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(dimenson.width / 2, dimenson.height / 2);
        frame.setLocationRelativeTo(null);
        addFrameContent();

        node = new Node(path, port);
        node.runServer();
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

        JButton makeConnection = new JButton("Ligar a Nó");
        JButton downloadButton = new JButton("Descarregar");
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));
        buttonsPanel.add(makeConnection);
        buttonsPanel.add(downloadButton);

        frame.add(searchPanel, BorderLayout.NORTH);
        frame.add(listPane, BorderLayout.CENTER);
        frame.add(buttonsPanel, BorderLayout.SOUTH);

        searchButton.addActionListener((ActionEvent e) -> searchFiles(searchField.getText()));
        makeConnection.addActionListener((ActionEvent e) -> connectToNodeDialog());
        downloadButton.addActionListener((ActionEvent e) -> downloadSelectedFile(list.getSelectedValue()));

        frame.setVisible(true);
    }

    public void searchFiles(String search) {
        resultsList.clear();
        if (search == null || search.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Por favor insira um texto válido para procurar.");
            return;
        }
        for (Node connectedNode : connectedNodes) {
            for (File file : connectedNode.getFiles()) {
                if (file.getName().contains(search)) {
                    resultsList.addElement(file.getName());
                }
            }
        }
    }

    public void connectToNodeDialog() {
        JTextField ipField = new JTextField();
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

    public static void main(String[] args) {
        @SuppressWarnings("unused")
        IscTorrent iscTorrent = new IscTorrent(args[0],Integer.parseInt(args[1]));
    }
}
