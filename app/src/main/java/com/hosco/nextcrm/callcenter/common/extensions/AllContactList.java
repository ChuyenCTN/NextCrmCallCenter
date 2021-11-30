package com.hosco.nextcrm.callcenter.common.extensions;

import com.hosco.nextcrm.callcenter.model.response.ContactResponse;
import com.hosco.nextcrm.callcenter.model.response.DeviceContact;
import com.hosco.nextcrm.callcenter.model.response.InternalResponse;

import java.util.ArrayList;
import java.util.List;

public class AllContactList {

    private static AllContactList instance;

    public static AllContactList getInstance() {
        if (instance == null)
            instance = new AllContactList();
        return instance;
    }

    private List<DeviceContact> allContacts = new ArrayList<>();

    public boolean isAddDevice = false;

    String temporaryPhone = "";

    String temporaryName = "";

    public void addCustomerToListContact(List<ContactResponse> data) {
        if (data != null && !data.isEmpty())
            for (ContactResponse contact : data) {
                if (contact.getName() != null && !contact.getName().isEmpty() && contact.getMobile() != null && !contact.getMobile().isEmpty())
                    allContacts.add(new DeviceContact(contact.getName(), contact.getMobile()));
            }
    }

    public void addDeviceToListContact(List<DeviceContact> data) {
        if (data == null)
            return;
        if (data.isEmpty())
            return;
        if (!isAddDevice) {
            allContacts.addAll(data);
        }
    }

    public void addDeviceToListContact(DeviceContact data) {
        if (data == null)
            return;
        if (!isAddDevice) {
            allContacts.add(data);
        }
    }

    public void addInternalToListContact(List<InternalResponse> data) {
        if (data != null && !data.isEmpty())
            for (InternalResponse contact : data) {
                String name = "";
                if (contact.getFirstname() != null)
                    name = contact.getFirstname();
                if (contact.getLastname() != null)
                    name = name + " " + contact.getLastname();
                name = name.replace("  ", " ").trim();
                if (contact.getCrmExtensionId() != null && !contact.getCrmExtensionId().isEmpty())
                    allContacts.add(new DeviceContact(name, contact.getCrmExtensionId()));
            }
    }

    public DeviceContact getContact(String phone) {
        if (allContacts != null && allContacts.size() > 0) {
            for (DeviceContact deviceContact : allContacts) {
                if (phone.replace(" ", "").trim().equalsIgnoreCase(deviceContact.getPhone().replace(" ", "").trim())) {
                    this.temporaryName = deviceContact.getName();
                    return deviceContact;
                }
            }
        }
        return null;
    }

    public List<DeviceContact> getAllContacts() {
        return allContacts;
    }

    public void setTemporaryPhone(String phone) {
        if (phone != null)
            this.temporaryPhone = phone;
    }

    public String getTemporaryPhone() {
        return temporaryPhone;
    }

    public String getName() {
        return temporaryName;
    }

    public void getContactRemote() {

    }


}
