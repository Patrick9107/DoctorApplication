package domain.User;

import domain.common.Image;

import java.util.Set;

public class Profile {

    // not null, min 1 character, max 128 characters
    private String name;
    // not null, min 1 character, max 128 characters
    private String title;
    // not null, not blank, TODO maybe have all countries listed in a set loaded from a txt file
    private String location;
    // not null, TODO max
    private Set<Specialization> tags;
    private Integer rating;
    private Image avatar;

    public Profile(String name, String title, String location, Set<Specialization> tags, Integer rating, Image avatar) {
        this.name = name; // TODO
        this.title = title; // TODO
        this.location = location; // TODO
        this.tags = tags; // TODO
        this.rating = rating; // TODO
        this.avatar = avatar; // TODO
    }
}
