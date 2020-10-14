package com.example.represent;

public class CivicData {
    private String image, name, party, link, location, address;

    public CivicData(String user) {
        address = user;
    }

    public CivicData(String image, String name, String party, String location, String link) {
        this.image = image;
        this.name = name;
        this.party = party;
        this.link = link;
        this.location = location;
    }

    // Generate getters.
    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getParty() {
        return party;
    }

    public String getLink() {
        return link;
    }

    public String getLocation() {
        return location;
    }
}
