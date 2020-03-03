package tel.panfilov.aspectj;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
class LazyFieldTest {

    @Test
    void testInterTypeDeclaration() {
        String randomString = String.valueOf(System.currentTimeMillis());
        TestObject object = new TestObject(randomString);
        assertThat(object, is(instanceOf(ILazyContainer.class)));
    }

    @Test
    void testInitialValueIsNull() throws Exception {
        String randomString = String.valueOf(System.currentTimeMillis());
        TestObject object = new TestObject(randomString);
        ILazyContainer container = (ILazyContainer) object;
        Field valueField = TestObject.class.getDeclaredField("value");
        valueField.setAccessible(true);
        assertThat(container.isInitialized(valueField), is(false));
        assertThat(valueField.get(container), is(nullValue()));
    }


    @Test
    void testLazyInitialization() {
        String randomString = String.valueOf(System.currentTimeMillis());
        TestObject object = new TestObject(randomString);
        assertThat(object, hasProperty("value", is(randomString)));
    }

    @Test
    void testPostInitialization() throws Exception {
        String randomString = String.valueOf(System.currentTimeMillis());
        TestObject object = new TestObject(randomString);
        ILazyContainer container = (ILazyContainer) object;
        Field valueField = TestObject.class.getDeclaredField("value");
        assertThat(object, hasProperty("value", is(randomString)));
        assertThat(container.isInitialized(valueField), is(true));
    }

    @Test
    void testSetter() throws Exception {
        String randomString = String.valueOf(System.currentTimeMillis());
        TestObject object = new TestObject(randomString);
        ILazyContainer container = (ILazyContainer) object;
        Field valueField = TestObject.class.getDeclaredField("value");
        randomString += ":modified";
        object.setValue(randomString);
        assertThat(container.isInitialized(valueField), is(true));
        assertThat(object, hasProperty("value", is(randomString)));
    }

}
