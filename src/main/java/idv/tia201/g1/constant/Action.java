package idv.tia201.g1.constant;

public enum Action {

    INVAILD(1);

    private int value;

    private Action(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
