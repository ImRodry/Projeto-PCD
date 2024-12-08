package pt.iscte.pcd.IscTorrent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;

public class IscTorrent extends JFrame {
    private Node node;
    DefaultListModel<ListFile> resultsList;
    HashMap<Integer, ArrayList<FileSearchResult>> fileSearchResults = new HashMap<>();

    private IscTorrent(String path, int port) {
        super("IscTorrent [path=" + path + ", ip=localhost: " + port + "]");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(dimension.width / 2, dimension.height / 2);
        this.setLocationRelativeTo(null);
        addFrameContent();

        node = new Node(path, port, this);
        node.runServer();
    }

    public Node getNode() {
        return node;
    }

    public void addFrameContent() {
        this.setLayout(new BorderLayout());

        JLabel searchText = new JLabel("Texto a procurar:");
        JTextField searchField = new JTextField(30);
        JButton searchButton = new JButton("Procurar");
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(searchText);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        resultsList = new DefaultListModel<>();
        JList<ListFile> list = new JList<>(resultsList);
        JScrollPane listPane = new JScrollPane(list);

        JButton downloadButton = new JButton("Descarregar");
        JButton makeConnection = new JButton("Ligar a Nó");
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 1));
        buttonsPanel.add(downloadButton);
        buttonsPanel.add(makeConnection);

        this.add(searchPanel, BorderLayout.NORTH);
        this.add(listPane, BorderLayout.CENTER);
        this.add(buttonsPanel, BorderLayout.EAST);

        searchButton.addActionListener((ActionEvent e) -> searchFiles(searchField.getText()));
        makeConnection.addActionListener((ActionEvent e) -> connectToNodeDialog());
        downloadButton.addActionListener((ActionEvent e) -> downloadSelectedFile(list.getSelectedValue()));

        this.setVisible(true);
    }

    public void searchFiles(String search) {
        if (search == null || search.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor insira um texto válido para procurar.");
            return;
        }
        // Clear both the map and the list to delete the old results and not display
        // outdated data in case no results are found
        fileSearchResults.clear();
        resultsList.clear();
        WordSearchMessage message = new WordSearchMessage(search);
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
                if (node.connectToNode(ip, Integer.parseInt(port)))
                    JOptionPane.showMessageDialog(this, "Ligado ao endereço: " + ip + " Porta: " + port);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao ligar ao nó.");
            }
        }
    }

    synchronized public void updateSearchResults(ArrayList<FileSearchResult> results) {
        // Clear the list to print everything with updated values
        resultsList.clear();
        for (FileSearchResult result : results) {
            // Don't display files the node already has
            if (node.getFiles().containsKey(result.getHash()))
                continue;
            fileSearchResults.computeIfAbsent(result.getHash(), k -> new ArrayList<>()).add(result);
        }
        for (ArrayList<FileSearchResult> result : fileSearchResults.values()) {
            FileSearchResult first = result.getFirst();
            resultsList.addElement(new ListFile(first.getFileName(),
                    result.stream().map(FileSearchResult::getOriginPort).toList(), first.getHash(),
                    first.getFileSize()));
        }
    }

    public void downloadSelectedFile(ListFile file) {
        if (file == null) {
            JOptionPane.showMessageDialog(this, "Por favor selecione um ficheiro para descarregar.");
            return;
        }
        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            if (node.download(file.getHash(), file.getFileSize(), file.getName(), file.getNodePorts())) {
                System.out.println("Download took: " + (System.currentTimeMillis() - startTime) + "ms");
                JOptionPane.showMessageDialog(this, "Ficheiro descarregado com sucesso.");
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao descarregar o ficheiro.");
            }
        }).start();
    }

    public void removeConnection(int port) {
        node.removeConnection(port);
        JOptionPane.showMessageDialog(this, "Ligação ao nó " + port + " removida.");
    }

    public static void main(String[] args) {
        @SuppressWarnings("unused")
        IscTorrent iscTorrent = new IscTorrent(args[0], Integer.parseInt(args[1]));
    }
}
