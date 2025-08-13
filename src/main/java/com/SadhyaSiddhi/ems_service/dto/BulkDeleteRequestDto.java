package com.SadhyaSiddhi.ems_service.dto;

import java.util.List;

public class BulkDeleteRequestDto {
    private List<String> users;

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}