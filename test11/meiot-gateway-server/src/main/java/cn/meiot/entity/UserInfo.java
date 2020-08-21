package cn.meiot.entity;

import lombok.Data;

import java.util.List;

@Data
public class UserInfo {

    private Integer id;

    private String userName;

    private String password;

    private String authentication;

    private List<String> roles;

    private List<String> permissions;

}
