package tel.panfilov.aspectj;

import java.lang.reflect.Field;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface ILazyContainer {

    boolean isInitialized(Field field);

}