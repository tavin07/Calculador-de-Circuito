package calculadorCircuito;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.util.Locale;

public class CalculadorCircuito extends JFrame {

    private JTextField txtR1, txtR2, txtTensao, txtTempo;
    private JLabel lblRespostaA, lblRespostaB, lblRespostaC;
    private JTextArea txtMemoriaCalculo;

    private static final double MAX_RESISTENCIA = 100000.0; // 100 kΩ
    private static final double MAX_TENSAO = 60.0;          // 60 V
    private static final double MAX_TEMPO = 60.0;           // 60 minutos
    private static final Locale LOCALE_BR = new Locale("pt", "BR");

    private static final Font FONTE_ROTULO = new Font("Arial", Font.BOLD, 16);
    private static final Font FONTE_CAMPO = new Font("Arial", Font.BOLD, 16);
    private static final Font FONTE_RESULTADO = new Font("Arial", Font.BOLD, 16);
    private static final Font FONTE_CALCULOS = new Font("Arial", Font.BOLD, 16);

    public CalculadorCircuito() {
        setTitle("Calculador de Circuito");
        setSize(480, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        //ENTRADAS
        JPanel painelEntradas = new JPanel(new GridLayout(4, 2, 10, 10));
        painelEntradas.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtR1 = criarCampoEntrada(painelEntradas, "Resistor R1 (Ω)", "Máx: 100.000 Ω");
        txtR2 = criarCampoEntrada(painelEntradas, "Resistor R2 (Ω)", "Máx: 100.000 Ω");
        txtTensao = criarCampoEntrada(painelEntradas, "Tensão da Fonte E (V)", "Máx: 60 V");
        txtTempo = criarCampoEntrada(painelEntradas, "Tempo de análise (min)", "Máx: 60 min");

        // SAIDAS
        JPanel painelResultados = new JPanel(new GridLayout(3, 2, 10, 10));
        painelResultados.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        lblRespostaA = criarCampoSaida(painelResultados, " (a) Valor absoluto de i1:");
        lblRespostaB = criarCampoSaida(painelResultados, " (b) Sentido de i1:");
        lblRespostaC = criarCampoSaida(painelResultados, " (c) Energia dissipada:");

        //CÁLCULOS
        txtMemoriaCalculo = new JTextArea();
        txtMemoriaCalculo.setEditable(false);
        txtMemoriaCalculo.setLineWrap(true);
        txtMemoriaCalculo.setWrapStyleWord(true);
        txtMemoriaCalculo.setFont(FONTE_CALCULOS);
        txtMemoriaCalculo.setMargin(new Insets(8, 8, 8, 8));
        JScrollPane scrollMemoria = new JScrollPane(txtMemoriaCalculo);
        scrollMemoria.setBorder(BorderFactory.createTitledBorder("Cálculos"));
        scrollMemoria.setPreferredSize(new Dimension(400, 150));

        JPanel painelCentral = new JPanel(new BorderLayout(5, 5));
        painelCentral.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        painelCentral.add(painelResultados, BorderLayout.NORTH);
        painelCentral.add(scrollMemoria, BorderLayout.CENTER);

        //BOTÕES
        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotao.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        JButton btnCalcular = new JButton("Calcular Respostas");
        btnCalcular.setFont(FONTE_ROTULO);
        btnCalcular.addActionListener(e -> calcularCircuito());
        painelBotao.add(btnCalcular);

        add(painelEntradas, BorderLayout.NORTH);
        add(painelCentral, BorderLayout.CENTER);
        add(painelBotao, BorderLayout.SOUTH);
    }

    //Cria um rótulo + campo de texto que só aceita dígitos e um ponto decimal
    private JTextField criarCampoEntrada(JPanel painel, String titulo, String maximoTexto) {
        String html = "<html><b>" + titulo + ":</b><br>"
                + "<span style='font-weight:normal; font-size:10px; color:gray;'>" + maximoTexto + "</span></html>";
        JLabel label = new JLabel(html);
        label.setFont(FONTE_ROTULO);
        painel.add(label);

        JTextField campo = new JTextField();
        campo.setFont(FONTE_CAMPO);
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new FiltroNumerico());
        painel.add(campo);
        return campo;
    }

    //cria um rótulo + campo de resposta (somente leitura), e devolve o label
    private JLabel criarCampoSaida(JPanel painel, String rotulo) {
        JLabel label = new JLabel(rotulo);
        label.setFont(FONTE_ROTULO);
        painel.add(label);

        JLabel resposta = new JLabel("-");
        resposta.setFont(FONTE_RESULTADO);
        painel.add(resposta);
        return resposta;
    }

    //filtro que só aceita dígitos e um único ponto decimal (sem checar limite máximo)
    private static class FiltroNumerico extends DocumentFilter {

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            replace(fb, offset, 0, string, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) return;

            if (text.isEmpty()) {
                super.replace(fb, offset, length, text, attrs);
                return;
            }

            String atual = fb.getDocument().getText(0, fb.getDocument().getLength());

            for (char c : text.toCharArray()) {
                if (!Character.isDigit(c) && c != '.') return;
            }
            if (text.contains(".") && atual.contains(".")) return;

            super.replace(fb, offset, length, text, attrs);
        }
    }

    private void calcularCircuito() {
        if (txtR1.getText().isEmpty() || txtR2.getText().isEmpty() ||
            txtTensao.getText().isEmpty() || txtTempo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.", "Campos Vazios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double r1, r2, tensao, tempoMinutos;
        try {
            r1 = Double.parseDouble(txtR1.getText());
            r2 = Double.parseDouble(txtR2.getText());
            tensao = Double.parseDouble(txtTensao.getText());
            tempoMinutos = Double.parseDouble(txtTempo.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valor inválido em algum campo.", "Erro de Leitura", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // VALIDAÇÃO L. MAXIMOS
        if (r1 > MAX_RESISTENCIA || r2 > MAX_RESISTENCIA) {
            JOptionPane.showMessageDialog(this, "As resistências não podem ultrapassar " + formatBR(MAX_RESISTENCIA, 0) + " Ω.", "Valor acima do limite", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (tensao > MAX_TENSAO) {
            JOptionPane.showMessageDialog(this, "A tensão não pode ultrapassar " + formatBR(MAX_TENSAO, 0) + " V.", "Valor acima do limite", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (tempoMinutos > MAX_TEMPO) {
            JOptionPane.showMessageDialog(this, "O tempo não pode ultrapassar " + formatBR(MAX_TEMPO, 0) + " minutos.", "Valor acima do limite", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // VALIDAÇÃO L. MÍNIMOS
        if (r1 < 0.1 || r2 < 0.1) {
            JOptionPane.showMessageDialog(this, "As resistências devem ser de pelo menos 0.1 Ω para evitar curto-circuito.", "Erro de Limite mínimo", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (tempoMinutos <= 0) {
            JOptionPane.showMessageDialog(this, "O tempo deve ser maior que zero.", "Erro de Limite mínimo", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //CALCULOS
        double r2Eq = r2 / 3.0;
        double rTotal = r1 + r2Eq;
        double iTotal = tensao / rTotal;
        double i1 = iTotal / 3.0;

        double potenciaTotal = tensao * iTotal;
        double tempoSegundos = tempoMinutos * 60.0;
        double energia = potenciaTotal * tempoSegundos;

        lblRespostaA.setText(String.format(Locale.US, "%.3f A", i1));
        lblRespostaB.setText("Para a direita");
        lblRespostaC.setText(String.format(Locale.US, "%.1f J", energia));

        txtMemoriaCalculo.setText(montarMemoriaCalculo(r1, r2, tensao, tempoMinutos, r2Eq, rTotal, iTotal, i1, potenciaTotal, tempoSegundos, energia));
        txtMemoriaCalculo.setCaretPosition(0);
    }

    private String formatBR(double valor, int casas) {
        return String.format(LOCALE_BR, "%." + casas + "f", valor);
    }

    private String montarMemoriaCalculo(double r1, double r2, double tensao, double tempoMinutos,
                                         double r2Eq, double rTotal, double iTotal, double i1,
                                         double potenciaTotal, double tempoSegundos, double energia) {
        StringBuilder sb = new StringBuilder();

        sb.append("R2,eq = R2 / 3 = ").append(formatBR(r2Eq, 2)).append(" Ω\n");
        sb.append("Rtotal = R1 + R2,eq = ").append(formatBR(rTotal, 1)).append(" Ω\n");
        sb.append("Itotal = E / Rtotal = ").append(formatBR(iTotal, 2)).append(" A\n");
        sb.append("i1 = Itotal / 3 = ").append(formatBR(i1, 3)).append(" A\n\n");

        sb.append("t = ").append(formatBR(tempoMinutos, 2)).append(" min = ")
          .append(formatBR(tempoSegundos, 1)).append(" s\n");
        sb.append("E = ε × Itotal × t = ").append(formatBR(energia, 0)).append(" J\n");

        return sb.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalculadorCircuito().setVisible(true));
    }
}