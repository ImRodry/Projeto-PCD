import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;

public class IscTorrent {
    private JFrame frame;
    private File[] files;
    private String port;

    public IscTorrent(String path, String port) {
        this.port = port;
        files = new File(path).listFiles((File file) -> file.isFile());

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

        makeConnection.addActionListener((ActionEvent e) -> {
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
                JOptionPane.showMessageDialog(IscTorrentGUI.this, 
                    "A ligar ao endereço: " + ip + " Porta: " + port);
                // In the full version, you'd attempt a connection here
            }
        });

        downloadButton.addActionListener((ActionEvent e) -> {
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
        });
    }

    public void open() {
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        IscTorrent iscTorrentGUI = new IscTorrent("src", "8888");
        iscTorrentGUI.open();
    }
}
