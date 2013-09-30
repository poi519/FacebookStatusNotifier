import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.Page;
import com.restfb.types.Post;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class Notifier {
    private static final String ACCESS_TOKEN = "222808124551239|INB1CFtr-1kMfd8-XTc_MrFHSeQ";
    FacebookClient publicClient = new DefaultFacebookClient(ACCESS_TOKEN);

    private Map<FacebookPage, Date> pageUpdates = new HashMap();

    public synchronized Map<FacebookPage, List<Post>> newPosts() {
        Map<FacebookPage, List<Post>> result = new HashMap();
        List<Post> feedPosts, feedNewPosts;
        Date updateTime;
        for(FacebookPage page : pageUpdates.keySet()) {
            updateTime = pageUpdates.get(page);
            feedNewPosts = new ArrayList();
            feedPosts = publicClient.fetchConnection(page.getId() + "/feed",Post.class).getData();
            for(Post post : feedPosts) {
                if(post.getCreatedTime().after(updateTime))
                    feedNewPosts.add(post);
            }
            result.put(page, feedNewPosts);
        }
        return result;
    }

    String getLinkToPost(Post post) {
        String[] ids = post.getId().split("_");
        String link = "http://facebook.com/" + ids[1];
        return link;
    }

    public synchronized Map<FacebookPage, Date> getPageUpdates() {
        return pageUpdates;
    }

    public void setPageUpdates(Map<FacebookPage, Date> pageUpdates) {
        this.pageUpdates = pageUpdates;
    }

    public boolean pageExists(FacebookPage page) {
        try {
            Page response = publicClient.fetchObject(page.getId(), Page.class);
            return(response != null);
        } catch (Exception e) {
            return false;
        }
    }

    public synchronized void addPage(FacebookPage page) {
        pageUpdates.put(page, new Date());
    }

    public synchronized void removePage(String id) {
        for(Iterator<Map.Entry<FacebookPage, Date>> it = pageUpdates.entrySet().iterator(); it.hasNext(); ){
            Map.Entry<FacebookPage, Date> entry = it.next();
            if (entry.getKey().getId().equals(id)) {
                System.out.println(id + " removed");
                it.remove();
            }
        }
    }

    public synchronized void update(FacebookPage p) {
        pageUpdates.put(p, new Date());
    }

    public synchronized void loadPages(String file) {
        pageUpdates.clear();
        DateFormat format = new SimpleDateFormat("MM/dd/yy HH:mm");
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String f, d;
            while((f = br.readLine()) != null &&
                    (d = br.readLine()) != null) {
                pageUpdates.put(new FacebookPage(f), format.parse(d));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public synchronized void savePages(String file) {
        DateFormat format = new SimpleDateFormat("MM/dd/yy HH:mm");
        try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), "utf-8"))) {
            for(Map.Entry<FacebookPage, Date> entry : pageUpdates.entrySet()) {
                writer.write(entry.getKey().getId() + "\n");
                writer.write(format.format(entry.getValue()) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}