package symgeom.geom;

import org.junit.Test;
import symgeom.value.Tribool;
import symgeom.value.Value;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static symgeom.value.Value.PI;
import static symgeom.value.Value.number;

public class TestValue
{
    private static final double DELTA = 1e-6;

    @Test
    public void test001()
    {
        Value value = number(42);
        assertEquals("42", value.toString());
        assertEquals(42.0, value.approximate(), DELTA);
        assertEquals(value, value.simplify());
    }

    @Test
    public void test002()
    {
        Value value = number(42).divide(number(11));
        assertEquals("42 / 11", value.toString());
        assertEquals(3.81818181818181, value.approximate(), DELTA);
        assertEquals(value, value.simplify());
    }

    @Test
    public void test003()
    {
        Value value = number(42).divide(number(6)).simplify();
        assertEquals("7", value.toString());
        assertEquals(7.0, value.approximate(), DELTA);
        assertEquals(value, value.simplify());
    }

    @Test
    public void test004()
    {
        Value value = PI.negate();
        assertEquals("-pi", value.toString());
        assertEquals(-3.141592654, value.approximate(), DELTA);
        assertEquals(value, value.simplify());
    }

    @Test
    public void test005()
    {
        Value value = Value.cos(Value.ZERO);
        assertEquals("cos(0)", value.toString());
        assertEquals(1.0, value.approximate(), DELTA);
        assertEquals(Value.ONE, value.simplify());
    }

    @Test
    public void test006()
    {
        Value value = Value.cos(PI);
        assertEquals("cos(pi)", value.toString());
        assertEquals(-1.0, value.approximate(), DELTA);
        assertEquals(number(-1), value.simplify());
    }

    @Test
    public void test007()
    {
        Value value = Value.sin(Value.ZERO);
        assertEquals("sin(0)", value.toString());
        assertEquals(0.0, value.approximate(), DELTA);
        assertEquals(Value.ZERO, value.simplify());
    }

    @Test
    public void test008()
    {
        Value value = Value.sin(PI);
        assertEquals("sin(pi)", value.toString());
        assertEquals(0.0, value.approximate(), DELTA);
        assertEquals(Value.ZERO, value.simplify());
    }

    @Test
    public void test009()
    {
        Value value = number(42).add(number(9)).simplify();
        assertEquals("51", value.toString());
        assertEquals(51.0, value.approximate(), DELTA);
        assertEquals(value, value.simplify());
    }

    @Test
    public void test010()
    {
        Value value = number(2_000_000_000).add(number(2_000_000_000));
        assertEquals("2000000000 + 2000000000", value.toString());
        assertEquals(4e9, value.approximate(), DELTA);
        assertEquals(value, value.simplify());
    }

    @Test
    public void test011()
    {
        Value value = number(342).divide(number(27)).simplify();
        assertEquals("38 / 3", value.toString());
        assertEquals(12.6666666666666, value.approximate(), DELTA);
        assertEquals(Value.fraction(38, 3), value.simplify());
    }

    @Test
    public void test012()
    {
        Value value = Value.sqrt(number(2)).divide(number(2));
        assertEquals("(DIVIDE (POWER 2 (DIVIDE 1 2)) 2)", value.toPrefix());
        assertEquals("sqrt(2) / 2", value.toString());
        assertEquals(0.707106781, value.approximate(), DELTA);
        assertEquals(Value.sqrt(number(2)).divide(number(2)), value.simplify());
    }

    @Test
    public void test013()
    {
        Value value = PI.add(number(2)).divide(number(3).add(PI));
        assertEquals("(pi + 2) / (3 + pi)", value.toString());
        assertEquals(0.8371757854348, value.approximate(), DELTA);
        assertEquals(PI.add(number(2)).divide(number(3).add(PI)), value.simplify());
    }

    @Test
    public void test014()
    {
        assertEquals(Tribool.TRUE, PI.eq(PI.power(Value.ONE)));
    }

    @Test
    public void test015()
    {
        assertTrue(PI.multiply(number(2)).eq(number(2).multiply(PI)).isTrue());
    }

    @Test
    public void test016()
    {
        assertFalse(PI.divide(number(2)).eq(number(2).divide(PI)).isTrue());
    }

    @Test
    public void test017()
    {
        Value value = number(42).power(Value.ZERO).simplify();
        assertEquals("1", value.toString());
        assertEquals(1.0, value.approximate(), DELTA);
        assertEquals(Value.ONE, value.simplify());
    }

    @Test
    public void test018()
    {
        Value value = number(42).power(Value.ONE).simplify();
        assertEquals("42", value.toString());
        assertEquals(42.0, value.approximate(), DELTA);
        assertEquals(number(42), value.simplify());
    }

    @Test
    public void test019()
    {
        Value value = PI.add(Value.ONE).square();
        assertEquals("(pi + 1) ^ 2", value.toString());
        assertEquals(17.152789708268, value.approximate(), DELTA);
        assertEquals(value, value.simplify());
    }

    @Test
    public void test020()
    {
        Value value = number(100).sqrt().multiply(number(-3));
        assertEquals(Tribool.TRUE, value.isStrictlyNegative());
    }

    @Test
    public void test021()
    {
        Value value = number(4).multiply(number(3).multiply(number(2).sqrt())).simplify();
        assertEquals(number(12).multiply(number(2).sqrt()), value);
    }

    @Test
    public void test022()
    {
        Value value = number(200).multiply(PI).divide(number(10)).simplify();
        assertEquals(number(20).multiply(PI), value);
    }

    @Test
    public void test023()
    {
        Value value = number(400).add(number(200).multiply(PI)).divide(number(30)).simplify();
        assertEquals(number(40).add(number(20).multiply(PI)).divide(number(3)), value);
    }
}
