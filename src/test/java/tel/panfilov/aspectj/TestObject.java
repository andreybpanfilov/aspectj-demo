package tel.panfilov.aspectj;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class TestObject {

    private String initialValue;

    public TestObject() {
        this(null);
    }

    public TestObject(String initialValue) {
        this.initialValue = initialValue;
    }

    @LazyField(computeMethod = "computeValue")
    private String value;

    private String computeValue() {
        return initialValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
