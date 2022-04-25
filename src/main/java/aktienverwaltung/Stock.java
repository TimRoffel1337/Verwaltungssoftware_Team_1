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

    public Integer getPrice() {
        return values[values.length - 1];
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

    public Integer getMin() {
        Integer min = values[0];
        for (int i = 0; i < values.length; i++) {
            if (values[i] < min) {
                min = values[i];
            }
        }
        return min;
    }

    public Integer getMax() {
        Integer max = values[0];
        for (int i = 0; i < values.length; i++) {
            if (values[i] > max) {
                max = values[i];
            }
        }
        return max;
    }

    public float getAverage() {
        float sum = 0;
        for (int i = 0; i < values.length; i++) {
            sum += values[i];
        }

        return sum / values.length;
    }

    public Integer getCurrentPrice() {
        return values[values.length - 1];
    }
}
