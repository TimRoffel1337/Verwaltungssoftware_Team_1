package aktienverwaltung;

public class Stock {
    private Integer[] values;
    private String name;

    public Stock(String name, Integer[] values) {
        this.values = values;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Integer[] getValues() {
        return values;
    }

    public void setValues(Integer[] values) {
        this.values = values;
    }

    public void addValue(int length, Integer[] oldArray, int value) {
        Integer[] newValues = new Integer[length + 1];
        for (int i = 0; i < length; i++) {
            newValues[i] = oldArray[i];
        }
        newValues[length] = value;

        this.values = newValues;
    }
}
