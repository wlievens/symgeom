package symgeom.comparator;

import org.junit.Test;
import symgeom.value.Value;

import static org.junit.Assert.assertEquals;
import static symgeom.value.Value.*;

public class TestComparator
{
    private final Comparator comparator = new Comparator();

    @Test
    public void test001()
    {
        Value value1 = number(33);
        Value value2 = number(87);
        verify(Order.LESSER, value1, value2);
    }

    @Test
    public void test002()
    {
        Value value1 = Value.fraction(94, 3);
        Value value2 = number(87);
        verify(Order.LESSER, value1, value2);
    }

    @Test
    public void test003()
    {
        Value value1 = number(33).sqrt();
        Value value2 = number(87);
        verify(Order.LESSER, value1, value2);
    }

    @Test
    public void test004()
    {
        Value value1 = number(-33).power(number(3));
        Value value2 = number(33).power(number(3));
        verify(Order.LESSER, value1, value2);
    }

    @Test
    public void test005()
    {
        Value value1 = number(-33).power(number(2));
        Value value2 = number(33).power(number(2));
        verify(Order.EQUAL, value1, value2);
    }

    @Test
    public void test006()
    {
        Value value1 = PI;
        Value value2 = PI.power(ONE);
        verify(Order.EQUAL, value1, value2);
    }

    @Test
    public void test007()
    {
        Value value1 = number(2).add(fraction(1, 13).multiply(number(91).sqrt())).divide(number(5));
        System.out.println(value1.approximate());
        Value value2 = ZERO;
        verify(Order.GREATER, value1, value2);
    }

    @Test
    public void test008()
    {
        Value value1 = number(2).add(fraction(-1, 13).multiply(number(91).sqrt())).divide(number(5));
        Value value2 = ZERO;
        verify(Order.GREATER, value1, value2);
    }

    @Test
    public void test009()
    {
        Value value1 = PI;
        Value value2 = ZERO;
        verify(Order.GREATER, value1, value2);
    }

    @Test
    public void test010()
    {
        Value value1 = PI;
        Value value2 = number(3);
        verify(Order.GREATER, value1, value2);
    }

    @Test
    public void test011()
    {
        Value value1 = PI;
        Value value2 = number(4);
        verify(Order.LESSER, value1, value2);
    }

    private void verify(Order reference, Value value1, Value value2)
    {
        assertEquals(reference, comparator.compare(value1, value2));
        assertEquals(reference.invert(), comparator.compare(value2, value1));
    }
}
