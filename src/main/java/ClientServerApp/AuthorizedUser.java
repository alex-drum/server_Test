package ClientServerApp;

public class AuthorizedUser {
    private final String userName;

    public AuthorizedUser(String name, Handler handler) {
        this.userName = name;
        System.out.println("User \"" + this.userName + "\" is activated");


    }
}
