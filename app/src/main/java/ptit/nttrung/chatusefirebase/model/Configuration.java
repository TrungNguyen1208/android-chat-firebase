package ptit.nttrung.chatusefirebase.model;

/**
 * Created by TrungNguyen on 9/15/2017.
 */

public class Configuration {
    private String label;
    private String value;
    private int icon;

    public Configuration(String label, String value, int icon) {
        this.label = label;
        this.value = value;
        this.icon = icon;
    }

    public String getLabel() {
        return this.label;
    }

    public String getValue() {
        return this.value;
    }

    public int getIcon() {
        return this.icon;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
