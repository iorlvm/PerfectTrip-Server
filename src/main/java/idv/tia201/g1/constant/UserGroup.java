package idv.tia201.g1.constant;

public enum UserGroup {

    NORMAL(0),
    VIP(1);

    private int value;

    private UserGroup(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
