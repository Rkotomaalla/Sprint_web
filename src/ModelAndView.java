package modelandview;

import java.util.HashMap;

public class ModelAndView {
    // url de destination apres l appel de la methode
    String url;
    // le donnees a envoyer vers la view
    HashMap<String, Object> data = new HashMap<>();

    public ModelAndView(String url, HashMap<String, Object> data) {
        this.url = url;
        this.data = data;
    }

    public void AddObject(String key, Object value) {
        data.put(key, value);
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HashMap<String, Object> getData() {
        return this.data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

}
