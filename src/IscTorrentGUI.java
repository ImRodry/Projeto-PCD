package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;

public class IscTorrentGUI {
    private JFrame frame;
    private File[] files;

    public IscTorrentGUI() {
        frame = new JFrame("IscTorrent");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension dimenson = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(dimenson.width / 2, dimenson.height / 2);
        frame.setLocationRelativeTo(null);
        addFrameContent();
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

        DefaultListModel<String> resultsList = new DefaultListModel<>();
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

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultsList.clear();
                String search = searchField.getText();
                if (search != null && !search.isEmpty()) {
                    // TODO search for files
                    // TODO add the number of hosts that have the file
                    resultsList.addElement(search); // TODO complete file name
                } else {
                    JOptionPane.showMessageDialog(frame, "Por favor insira um texto válido para procurar.");
                }
            }
        });

        makeConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame connectionFrame = new JFrame("Adicionar Nó");
                connectionFrame.setLayout(new FlowLayout());
                connectionFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                JLabel ipLabel = new JLabel("Endereço:");
                JTextField ipField = new JTextField(15);
                JLabel portLabel = new JLabel("Porta:");
                JTextField portField = new JTextField(7);
                JButton cancelButton = new JButton("Cancelar");
                JButton connectButton = new JButton("OK");
                connectionFrame.add(ipLabel);
                connectionFrame.add(ipField);
                connectionFrame.add(portLabel);
                connectionFrame.add(portField);
                connectionFrame.add(cancelButton);
                connectionFrame.add(connectButton);
                connectionFrame.pack();
                connectionFrame.setLocationRelativeTo(null);
                connectionFrame.setVisible(true);

                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        connectionFrame.dispose();
                    }
                });

                connectButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String ip = ipField.getText();
                        String port = portField.getText();
                        if (ip != null && !ip.isEmpty() && port != null && !port.isEmpty()) {
                            // TODO connect to node
                            connectionFrame.dispose();
                        } else {
                            JOptionPane.showMessageDialog(connectionFrame,
                                    "Por favor insira um endereço e porta válidos.");
                        }
                    }
                });
            }
        });

        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedFile = list.getSelectedValue();
                // TODO remove the <#> from the file name
                if (selectedFile != null) {
                    // TODO download file
                    // TODO time taken and who provided the file
                    JOptionPane.showMessageDialog(frame,
                            "");
                } else {
                    JOptionPane.showMessageDialog(frame,
                            "Por favor selecione um ficheiro para descarregar.");
                }
            }
        });
    }

    public void open() {
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        IscTorrentGUI iscTorrentGUI = new IscTorrentGUI();
        iscTorrentGUI.open();
    }
}
