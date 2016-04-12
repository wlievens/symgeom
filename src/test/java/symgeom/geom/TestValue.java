package symgeom.geom;

import org.junit.Test;
import symgeom.value.Tribool;
import symgeom.value.Value;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static symgeom.value.Value.*;

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
        assertEquals(fraction(38, 3), value.simplify());
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

    @Test
    public void test024()
    {
        Value value = number(42).multiply(PI.divide(E));
        assertEquals(48.54054869, value.approximate(), DELTA);
        assertEquals("42 * (pi / e)", value.toString());
    }

    @Test
    public void test025()
    {
        Value value = number(42).divide(PI.multiply(E));
        assertEquals(4.918185848, value.approximate(), DELTA);
        assertEquals("42 / (pi * e)", value.toString());
    }

    @Test
    public void test026()
    {
        Value value = number(320).divide(number(13).multiply(number(40))).simplify();
        assertEquals(0.615384615, value.approximate(), DELTA);
        assertEquals("8 / 13", value.toString());
    }

    @Test
    public void test027()
    {
        Value value = number(2).sqrt().power(number(2)).simplify();
        assertEquals(2.0, value.approximate(), DELTA);
        assertEquals("2", value.toString());
    }

    @Test
    public void test028()
    {
        Value value = number(50).multiply(number(2).sqrt()).power(number(2)).simplify();
        assertEquals(5000.0, value.approximate(), DELTA);
        assertEquals("5000", value.toString());
    }

    @Test
    public void test029()
    {
        Value value = fraction(1, 2).multiply(number(2)).simplify();
        assertEquals(1.0, value.approximate(), DELTA);
        assertEquals("1", value.toString());
    }

    @Test
    public void test030()
    {
        Value value = number(36).add(PI).subtract(number(40)).simplify();
        assertEquals(-0.858407346, value.approximate(), DELTA);
        assertEquals("-4 + pi", value.toString());
    }

    @Test
    public void test031()
    {
        Value value = number(36).add(PI.subtract(number(40))).simplify();
        assertEquals(-0.858407346, value.approximate(), DELTA);
        assertEquals("-4 + pi", value.toString());
    }

    @Test
    public void test032()
    {
        Value value = number(-4).add(number(-4).multiply(number(11).sqrt())).divide(number(40)).simplify();
        assertEquals(-0.43166248, value.approximate(), DELTA);
        assertEquals("-1 / 10 + (-1 / 10) * sqrt(11)", value.toString());
    }

    @Test
    public void test033()
    {
        Value value = number(-2).divide(number(-1).divide(number(13))).simplify();
        assertEquals(26, value.approximate(), DELTA);
        assertEquals("26", value.toString());
    }

    @Test
    public void test034()
    {
        Value value = number(2).add(fraction(-1, 13).multiply(number(91).sqrt())).divide(number(5));
        assertEquals(0.2532401228, value.approximate(), DELTA);
        assertEquals(Tribool.FALSE, value.lt(Value.ZERO));
        assertEquals(Tribool.TRUE, value.lt(Value.ONE));
    }

    @Test
    public void test035()
    {
        Value value = number(2).sqrt();
        assertEquals(Tribool.FALSE, value.isStrictlyNegative());
    }

    @Test
    public void test036()
    {
        assertEquals(Tribool.FALSE, number(91).sqrt().lt(number(-39)));
        assertEquals(Tribool.TRUE, number(-39).lt(number(91)));
    }

    @Test
    public void test037()
    {
        assertEquals(Tribool.TRUE, number(91).sqrt().lt(number(26)));
    }

    @Test
    public void test038()
    {
        Value x = number(4).add(number(-15).divide(number(37)).add(number(7).divide(number(37)).multiply(number(287).sqrt()))).divide(number(14));
        Value y = number(10).add(number(105).divide(number(37)).add(number(25).divide(number(37)).multiply(number(287).sqrt()))).divide(number(50));
        System.out.println("X: " + x);
        System.out.println("Y: " + y);
        System.out.println("X simplified: " + x.simplify());
        System.out.println("Y simplified: " + y.simplify());
        assertEquals(Tribool.TRUE, x.eq(y));
    }

    @Test
    public void test039()
    {
        Value value = fraction(-5, 37).multiply(number(2).sqrt().negate());
        assertEquals("(5 / 37) * sqrt(2)", value.simplify().toString());
    }

    @Test
    public void test040()
    {
        Value x = number(10).multiply(number(2).add(number(-15).divide(number(74)).add(number(7).divide(number(74)).multiply(number(287).sqrt()))));
        Value y = number(7).multiply(number(2).add(number(21).divide(number(37)).add(number(5).divide(number(37)).multiply(number(287).sqrt()))));
        System.out.println("X: " + x);
        System.out.println("Y: " + y);
        System.out.println("X simplified: " + x.simplify());
        System.out.println("Y simplified: " + y.simplify());
        assertEquals(Tribool.TRUE, x.eq(y));
    }

}
