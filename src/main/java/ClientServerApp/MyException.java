package ClientServerApp;

public class MyException extends Exception {
    private static String msg;

    public MyException(Exception e) {
                    StackTraceElement elements[] = e.getStackTrace();
            for (int i = 0, n = elements.length; i < n; i++) {
                msg = "{\"Error code:\"" + (elements[i].getClassName()
                        + elements[i].getLineNumber())
                        + "\",\"Method caused\":\""
                        + elements[i].getMethodName() + "\"}";
            }

    }
    public void print() {
        System.out.println("MyException: ");
        System.out.println(msg);
    }
}
