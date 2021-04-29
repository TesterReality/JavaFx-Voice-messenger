package sample;

public class FlowFieldPositionHelper {

    private int position =0;
    private int caretPosition =0;
    public FlowFieldPositionHelper() {
        super();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getCaretPosition() {
        return caretPosition;
    }

    public void setCaretPosition(int caretPosition) {
        System.out.println("CaretPosition: "+ caretPosition);
        this.caretPosition = caretPosition;
    }
}
