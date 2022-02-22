package day01;

public class Actor {

    private long id;
    private String name;

    public Actor(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
