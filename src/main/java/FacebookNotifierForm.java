import com.restfb.types.Post;

import javax.swing.*;
import java.awt.event.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FacebookNotifierForm {
    private static String PAGE_FILE = "pages.dat";

    private JPanel panel;
    private JList list1;
    private JTextField textField1;
    private JButton addButton;
    private JButton removeButton;

    private DefaultListModel<String> listModel = new DefaultListModel<String>();

    private Notifier notifier;

    public void setData(FacebookPage data) {
        textField1.setText(data.getId());
    }

    public void getData(FacebookPage data) {
        data.setId(textField1.getText());
    }

    private static String emptyIfNull(String s) {
        if(s == null)
            return "";
        else
            return s;
    }

    public FacebookNotifierForm(Notifier n) {
        notifier = n;
        list1.setModel(listModel);
        displayPages();
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String link = textField1.getText();
                if(!link.isEmpty()) {
                    FacebookPage page = FacebookPage.fromLink(link);
                    if(notifier.pageExists(page)) {
                        notifier.addPage(page);
                        listModel.addElement(page.getId());
                        textField1.setText("");
                    } else {
                        JOptionPane.showMessageDialog(null, "Page doesn't exist");
                    }
                }
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int index = list1.getSelectedIndex();
                if(index >= 0) {
                    String pageID = listModel.get(index);
                    notifier.removePage(pageID);
                    listModel.remove(index);
                }
            }
        });
    }

    void displayPages() {
        listModel.removeAllElements();
        for(FacebookPage page : notifier.getPageUpdates().keySet())
            listModel.addElement(page.getId());
    }

    public static void main(String[] args) {
        final Notifier notifier= new Notifier();
        notifier.loadPages(PAGE_FILE);

        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        final JFrame frame = new JFrame("Facebook Status Notifier");
        FacebookNotifierForm form = new FacebookNotifierForm(notifier);
        frame.setContentPane(form.panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                notifier.savePages(PAGE_FILE);
                super.windowClosing(e);
            }
        });
        frame.pack();
        frame.setVisible(true);
        while(true) {
            System.out.println("Updating");
            Map<FacebookPage, List<Post>> result = notifier.newPosts();
            for(FacebookPage feed : result.keySet()) {
                notifier.update(feed);
                for(Post post : result.get(feed)) {
                    new NotificationFrame(feed.getId(),
                            emptyIfNull(post.getCaption()) + "\n" + emptyIfNull(post.getMessage())
                            + emptyIfNull(post.getLink()),
                            notifier.getLinkToPost(post));
                }
            }
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
