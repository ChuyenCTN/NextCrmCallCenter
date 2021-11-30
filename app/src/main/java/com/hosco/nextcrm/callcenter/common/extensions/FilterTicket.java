package com.hosco.nextcrm.callcenter.common.extensions;

import com.hosco.nextcrm.callcenter.model.response.ContactResponse;
import com.sun.xml.bind.v2.schemagen.xmlschema.Any;

import java.util.ArrayList;

public class FilterTicket {

    private static FilterTicket instance;

    public static FilterTicket getInstance() {
        if (instance == null)
            instance = new FilterTicket();
        return instance;
    }

    public ContactResponse contactResponse;

    public String state = "";
   private ArrayList<Any> h= new ArrayList<Any>();

    public String priority = "";

    public String type = "";

    public static void setInstance(FilterTicket instance) {
        FilterTicket.instance = instance;
    }

    public ContactResponse getContactResponse() {
        return contactResponse;
    }

    public void setContactResponse(ContactResponse contactResponse) {
        this.contactResponse = contactResponse;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void refreshInput() {
        setType("");
        setState("");
        setPriority("");
        setContactResponse(null);
    }
}
