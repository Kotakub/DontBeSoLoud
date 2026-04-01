package dontbesoloud.general;

import java.util.*;

public class Group {
    public int id;
    public String name;
    public List<Lesson> lessons;

    public Group(int id, String name, List<Lesson> lessons) {
        this.id = id;
        this.name = name;
        this.lessons = lessons;
    }
}
