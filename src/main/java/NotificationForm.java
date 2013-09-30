import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

public class NotificationForm {
    private JButton button1;
    private JLabel label;
    public JPanel panel;
    private JTextArea textArea1;
    private JScrollPane scrollPane;

    NotificationForm(String caption, String text, final String link, final JFrame frame) {
        label.setText(caption);
        textArea1.setLineWrap(true);
        textArea1.setText(text);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    Desktop.getDesktop().browse(URI.create(link));
                    frame.dispose();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}

class NotificationFrame extends JFrame {
    static GraphicsEnvironment ge =
            GraphicsEnvironment.getLocalGraphicsEnvironment();
    static GraphicsDevice gd = ge.getDefaultScreenDevice();
    static boolean translucent = gd.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT);
    static Point position =
            new Point(gd.getDisplayMode().getWidth() - 430, gd.getDisplayMode().getHeight() - 200);


    NotificationFrame(String caption, String text, String link) {
        super(caption);
        setContentPane(new NotificationForm(caption, text, link, this).panel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setFocusable(false);
        if(translucent)
            setOpacity(0.9f);
        pack();
        setLocation(position);
        setVisible(true);
    }
}
