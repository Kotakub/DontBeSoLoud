package dontbesoloud.general;

public class Lesson {
    public int id;
    public String type; // "theory" или "practice"
    public String signName;
    public String description;
    public String photoUrl;

    public Lesson(int id, String type, String signName, String photoUrl, String description) {
        this.id = id;
        this.type = type;
        this.signName = signName;
        this.photoUrl = photoUrl;
        this.description = description;
    }
}
