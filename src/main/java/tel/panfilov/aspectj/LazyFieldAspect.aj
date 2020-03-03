package tel.panfilov.aspectj;

import org.aspectj.lang.reflect.FieldSignature;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.BitSet;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public aspect LazyFieldAspect {

    private static final Unsafe UNSAFE = getUnsafe();

    declare parents : hasfield(@LazyField * *) implements ILazyContainer;

    pointcut getAnnotatedWithLazyField(): get(@LazyField * *);

    pointcut setAnnotatedWithLazyField(): set(@LazyField * *);

    private BitSet ILazyContainer.lazyFieldStates = new BitSet();

    public boolean ILazyContainer.isInitialized(Field field) {
        long offset = UNSAFE.objectFieldOffset(field);
        return lazyFieldStates.get((int) offset);
    }

    private void ILazyContainer.setLazyFieldState(Field field, boolean value) {
        long offset = UNSAFE.objectFieldOffset(field);
        lazyFieldStates.set((int) offset, value);
    }

    private void ILazyContainer.initField(Field field) {
        try {
            LazyField lazyField = field.getAnnotation(LazyField.class);
            Method compute = getClass().getDeclaredMethod(lazyField.computeMethod());
            compute.setAccessible(true);
            Object value = compute.invoke(ILazyContainer.this);
            UNSAFE.putObject(ILazyContainer.this, UNSAFE.objectFieldOffset(field), value);
            ILazyContainer.this.setLazyFieldState(field, true);
        } catch (NoSuchMethodException | IllegalAccessException ex) {
            UNSAFE.throwException(ex);
        } catch (InvocationTargetException ex) {
            UNSAFE.throwException(ex.getTargetException());
        }
    }

    before(): getAnnotatedWithLazyField() {
        ILazyContainer object = (ILazyContainer) thisJoinPoint.getTarget();
        FieldSignature fieldSignature = (FieldSignature) thisJoinPoint.getSignature();
        Field field = fieldSignature.getField();

        if (object.isInitialized(field)) {
            return;
        }

        object.initField(field);
    }

    after(): setAnnotatedWithLazyField() {
        ILazyContainer object = (ILazyContainer) thisJoinPoint.getTarget();
        FieldSignature fieldSignature = (FieldSignature) thisJoinPoint.getSignature();
        Field field = fieldSignature.getField();

        if (object.isInitialized(field)) {
            return;
        }

        object.setLazyFieldState(field, true);
    }

    private static Unsafe getUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get unsafe", e);
        }
    }

}
