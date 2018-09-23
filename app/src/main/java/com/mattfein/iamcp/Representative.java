package com.mattfein.iamcp;

public class Representative {
    String name, party, state, district, phone, office, link;

    public Representative(String name, String party, String state, String district, String phone, String office, String link) {
        this.name = name;
        this.party = party;
        this.state = state;
        this.district = district;
        this.phone = phone;
        this.office = office;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public String getParty() {
        return party;
    }

    public String getState() {
        return state;
    }

    public String getDistrict() {
        return district;
    }

    public String getPhone() {
        return phone;
    }

    public String getOffice() {
        return office;
    }

    public String getLink() {
        return link;
    }


}
