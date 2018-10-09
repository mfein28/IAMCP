package com.mattfein.iamcp;

import java.util.Map;

public class Representative {
    String name, address, party, phones, urls, photourl;
    Map<String, String> channels;

    public Representative(String name, String address, String party, String phones, String urls, String photourl, Map<String, String> channels) {
        this.name = name;
        this.address = address;
        this.party = party;
        this.phones = phones;
        this.urls = urls;
        this.photourl = photourl;
        this.channels = channels;
    }

    public String getName() {
        return name;
    }

    public String getParty() {
        return party;
    }

    public String getAddress() {
        return address;
    }

    public String getPhones() {
        return phones;
    }

    public String getUrls() {
        return urls;
    }

    public String getPhotourl() {
        return photourl;
    }

    public Map<String, String> getChannels() {
        return channels;
    }
}
