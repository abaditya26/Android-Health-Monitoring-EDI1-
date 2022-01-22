package ml.adityabodhankar.androidhealthmonitoring.Models;

public class UserModel {
    private String uid, name, email, phone, gender, weight, height;

    public UserModel() {
    }

    public UserModel(String uid, String name, String email, String phone, String gender, String weight, String height) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.weight = weight;
        this.height = height;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }
}
