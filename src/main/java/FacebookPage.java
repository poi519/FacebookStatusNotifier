public class FacebookPage {
    private String id;

    public FacebookPage(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public static FacebookPage fromLink(String link) {
        String[] parts = link.split("/");
        String pageID = parts[parts.length - 1];
        return new FacebookPage(pageID);
    }
}