package dontbesoloud.general;

public class Lesson {
    private int id;
    private int groupId;
    private String type; // "theory" или "practice"
    private String signName;
    private int signId;
    private String description;
    private String photoUrl;

    public Lesson(int id, int groupId, String type, String signName, int signId, String photoUrl, String description) {
        setId(id);
        setGroupId(groupId);
        setType(type);
        this.signName = signName;
        setId(signId);
        this.photoUrl = photoUrl;
        this.description = description;
    }

    // Геттеры
    public int getId() {
        return id;
    }

    public int getGroupId() {
        return groupId;
    }

    public String getType() {
        return type;
    }

    public String getSignName() {
        return signName;
    }

    public int getSignId() {
        return signId;
    }

    public String getDescription() {
        return description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    // Сеттеры
    public void setId(int id) {
        if (id >= -1) {
            this.id = id;
        } else {
            System.err.println("Ошибка. Некорректный id: " + id);
        }
    }

    public void setGroupId(int groupId) {
        if (groupId > 0) {
            this.groupId = groupId;
        } else {
            System.err.println("Ошибка. Некорректный groupId: " + groupId);
        }
    }

    public void setType(String type) {
        if (type.equals("theory") || type.equals("practice")) {
            this.type = type;
        } else {
            System.err.println("Ошибка. Некорректный тип занятия: " + type);
        }
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public void setSignId(int signId) {
        if (signId > 0) {
            this.signId = signId;
        } else {
            System.err.println("Ошибка. Некорректный SignId: " + signId);
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}