package fecoder.models;

public class User {
    private int id;
    private String name;
    private String account;
    private String password;
    private int roleId;

    public User() {}

    public User(int id, String name, String account, String password, int role){
        this.id = id;
        this.name=name;
        this.account = account;
        this.password=password;
        this.roleId=role;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getRoleId() { return roleId; }
    public void setRoleId(int id) { this.roleId = id; }
}
