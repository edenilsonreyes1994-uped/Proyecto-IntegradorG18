package semana18.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.SwingUtilities;

public class VentanaConsulta extends JFrame {
    private JButton btnConsultar = new JButton("Consultar socios");
    private JLabel lblEstado = new JLabel("Listo para consultar");
    private JTextArea areaDatos = new JTextArea(10, 30);
    private static final String URL = "http://localhost:8080/socios";

    public VentanaConsulta() {
        super("Consulta al servidor");
        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        panelNorte.add(btnConsultar);
        panelNorte.add(lblEstado);

        areaDatos.setFont(new Font("Monospaced", Font.PLAIN, 13));
        areaDatos.setEditable(false);

        add(panelNorte, BorderLayout.NORTH);
        add(new JScrollPane(areaDatos), BorderLayout.CENTER);

        btnConsultar.addActionListener(e -> consultar());

        setSize(450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void consultar() {
        btnConsultar.setEnabled(false);
        lblEstado.setText("Consultando...");
        areaDatos.setText("");

        SwingWorker<String, Void> trabajador = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                HttpClient cliente = HttpClient.newHttpClient();
                HttpRequest peticion = HttpRequest.newBuilder(URI.create(URL)).build();
                HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());
                return respuesta.body();
            }

            @Override
            protected void done() {
                try {
                    String json = get();
                    areaDatos.setText(json.replace("}, {", "}\n{"));
                    lblEstado.setText("Datos recibidos");
                } catch (InterruptedException | ExecutionException ex) {
                    lblEstado.setText("Error");
                    JOptionPane.showMessageDialog(VentanaConsulta.this,
                            "No se pudo conectar con el servidor",
                            "Error de conexion", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnConsultar.setEnabled(true);
                }
            }
        };
        trabajador.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaConsulta().setVisible(true));
    }
}