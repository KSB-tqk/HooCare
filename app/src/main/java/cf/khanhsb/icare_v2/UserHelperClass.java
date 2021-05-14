package cf.khanhsb.icare_v2;

public class UserHelperClass {
    String mName, mUsername, mEmail, mPassword;

    public UserHelperClass() {
    }

    public UserHelperClass(String mName, String mUsername, String mEmail, String mPassword) {
        this.mName = mName;
        this.mUsername = mUsername;
        this.mEmail = mEmail;
        this.mPassword = mPassword;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }
}
