package symgeom.simplifier;

import org.junit.Test;
import symgeom.value.Value;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static symgeom.value.Value.*;

public class TestSimplifier
{
    private final Simplifier simplifier = new Simplifier();

    @Test
    public void test001()
    {
        Value input = number(10).add(number(20));
        Value reference = number(30);
        verify(reference, input);
    }

    @Test
    public void test002()
    {
        Value input = PI.add(number(0));
        Value reference = PI;
        verify(reference, input);
    }

    @Test
    public void test003()
    {
        Value input = number(2000000000).add(number(1000000000));
        Value reference = number(BigInteger.valueOf(3000000000L));
        verify(reference, input);
    }

    @Test
    public void test004()
    {
        Value input = number(18).divide(number(27));
        Value reference = number(2).divide(number(3));
        verify(reference, input);
    }

    @Test
    public void test005()
    {
        Value input = number(18).divide(number(-27));
        Value reference = number(-2).divide(number(3));
        verify(reference, input);
    }

    @Test
    public void test006()
    {
        Value input = number(18).divide(number(1));
        Value reference = number(18);
        verify(reference, input);
    }

    @Test
    public void test007()
    {
        Value input = number(0).add(PI);
        Value reference = PI;
        verify(reference, input);
    }

    @Test
    public void test008()
    {
        Value input = PI.power(ZERO);
        Value reference = ONE;
        verify(reference, input);
    }

    @Test
    public void test009()
    {
        Value input = PI.power(ONE);
        Value reference = PI;
        verify(reference, input);
    }

    @Test
    public void test010()
    {
        Value input = cos(ZERO);
        Value reference = ONE;
        verify(reference, input);
    }

    @Test
    public void test011()
    {
        Value input = cos(PI);
        Value reference = number(-1);
        verify(reference, input);
    }

    @Test
    public void test012()
    {
        Value input = sin(ZERO);
        Value reference = ZERO;
        verify(reference, input);
    }

    @Test
    public void test013()
    {
        Value input = sin(PI);
        Value reference = ZERO;
        verify(reference, input);
    }

    @Test
    public void test014()
    {
        Value input = HALF.multiply(number(2));
        Value reference = number(1);
        verify(reference, input);
    }

    @Test
    public void test015()
    {
        Value input = (number(1).divide(PI)).multiply(PI);
        Value reference = number(1);
        verify(reference, input);
    }

    @Test
    public void test016()
    {
        Value input = number(2).sqrt().square();
        Value reference = number(2);
        verify(reference, input);
    }

    @Test
    public void test017()
    {
        Value input = number(1).multiply(PI);
        Value reference = PI;
        verify(reference, input);
    }

    @Test
    public void test018()
    {
        Value input = PI.multiply(number(1));
        Value reference = PI;
        verify(reference, input);
    }

    @Test
    public void test019()
    {
        Value input = number(2).multiply(number(3).multiply(PI));
        Value reference = number(6).multiply(PI);
        verify(reference, input);
    }

    @Test
    public void test020()
    {
        Value input = (number(2).multiply(number(3))).multiply(PI);
        Value reference = number(6).multiply(PI);
        verify(reference, input);
    }

    @Test
    public void test021()
    {
        Value input = (PI.multiply(number(3))).multiply(number(2));
        Value reference = number(6).multiply(PI);
        verify(reference, input);
    }

    @Test
    public void test022()
    {
        Value input = PI.negate().negate();
        Value reference = PI;
        verify(reference, input);
    }

    @Test
    public void test023()
    {
        Value input = PI.negate().negate().negate().negate().negate();
        Value reference = PI.negate();
        verify(reference, input);
    }

    @Test
    public void test024()
    {
        Value input = number(10000).multiply(number(1000000));
        Value reference = number(BigInteger.valueOf(10000000000L));
        verify(reference, input);
    }

    @Test
    public void test025()
    {
        Value input = number(4).square();
        Value reference = number(16);
        verify(reference, input);
    }

    @Test
    public void test026()
    {
        Value input = number(10000).multiply(number(1000).square());
        Value reference = number(BigInteger.valueOf(10000000000L));
        verify(reference, input);
    }

    @Test
    public void test027()
    {
        Value input = (number(200).multiply(PI)).divide(number(10));
        Value reference = number(20).multiply(PI);
        verify(reference, input);
    }

    @Test
    public void test028()
    {
        Value input = (PI.multiply(number(200))).divide(number(10));
        Value reference = number(20).multiply(PI);
        verify(reference, input);
    }

    @Test
    public void test029()
    {
        Value input = (PI.multiply(number(17))).divide(PI);
        Value reference = number(17);
        verify(reference, input);
    }

    @Test
    public void test030()
    {
        Value input = (number(17).multiply(PI)).divide(PI);
        Value reference = number(17);
        verify(reference, input);
    }

    @Test
    public void test031()
    {
        Value input = fraction(-5, 37).multiply(number(2).sqrt());
        Value reference = fraction(-5, 37).multiply(number(2).sqrt());
        verify(reference, input);
    }

    @Test
    public void test032()
    {
        Value input = number(-2).divide(number(-1).divide(number(13)));
        Value reference = number(26);
        verify(reference, input);
    }

    @Test
    public void test033()
    {
        Value input = fraction(-5, 37).multiply(number(2).sqrt().negate());
        Value reference = fraction(5, 37).multiply(number(2).sqrt());
        verify(reference, input);
    }

    @Test
    public void test034()
    {
        Value input = fraction(5, 37).negate();
        Value reference = fraction(-5, 37);
        verify(reference, input);
    }

    @Test
    public void test035()
    {
        Value input = number(36).add(PI).subtract(number(40));
        Value reference = number(-4).add(PI);
        verify(reference, input);
    }

    @Test
    public void test036()
    {
        Value input = number(36).subtract(number(40));
        Value reference = number(-4);
        verify(reference, input);
    }

    @Test
    public void test037()
    {
        Value input = number(2).sqrt().square();
        Value reference = number(2);
        verify(reference, input);
    }

    @Test
    public void test038()
    {
        Value input = number(2).sqrt().multiply(number(2).sqrt());
        Value reference = number(2);
        verify(reference, input);
    }

    @Test
    public void test039()
    {
        Value input = number(400).add(number(200).multiply(PI)).divide(number(30)).simplify();
        Value reference = number(40).add(number(20).multiply(PI)).divide(number(3));
        verify(reference, input);
    }

    @Test
    public void test040()
    {
        Value input = fraction(0, 16);
        Value reference = ZERO;
        verify(reference, input);
    }

    @Test
    public void test041()
    {
        Value input = number(3400).sqrt();
        Value reference = number(10).multiply(number(34).sqrt());
        verify(reference, input);
    }

    @Test
    public void test042()
    {
        Value input = number(50).multiply(number(2).sqrt()).power(number(2));
        Value reference = number(5000);
        verify(reference, input);
    }

    private void verify(Value reference, Value input)
    {
        Value output = simplifier.simplify(input);
        assertEquals(reference, output);
    }
}
