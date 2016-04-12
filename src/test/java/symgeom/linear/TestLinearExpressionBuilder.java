package symgeom.linear;

import org.junit.Test;
import symgeom.value.Value;

import static org.junit.Assert.assertEquals;
import static symgeom.value.Value.*;

public class TestLinearExpressionBuilder
{
    @Test
    public void testBuild001()
    {
        LinearExpression expression = new LinearExpressionBuilder().build(PI);
        assertEquals("Linear[1/1 {pi}]", expression.toString());
    }

    @Test
    public void testBuild002()
    {
        LinearExpression expression = new LinearExpressionBuilder().build(ONE.add(PI));
        assertEquals("Linear[1/1 {1} + 1/1 {pi}]", expression.toString());
    }

    @Test
    public void testBuild003()
    {
        LinearExpression expression = new LinearExpressionBuilder().build(number(31).divide(number(42)));
        assertEquals("Linear[31/42 {1}]", expression.toString());
    }

    @Test
    public void testBuild004()
    {
        LinearExpression expression = new LinearExpressionBuilder().build(number(31).divide(number(42)).multiply(PI));
        assertEquals("Linear[31/42 {pi}]", expression.toString());
    }

    @Test
    public void testBuild005()
    {
        LinearExpression expression = new LinearExpressionBuilder().build(PI.divide(number(42)));
        assertEquals("Linear[1/42 {pi}]", expression.toString());
    }

    @Test
    public void testBuild006()
    {
        LinearExpression expression = new LinearExpressionBuilder().build(number(42).divide(PI));
        assertEquals("Linear[42/1 {1 / pi}]", expression.toString());
    }

    @Test
    public void testBuild007()
    {
        LinearExpression expression = new LinearExpressionBuilder().build(number(42).multiply(PI));
        assertEquals("Linear[42/1 {pi}]", expression.toString());
    }

    @Test
    public void testBuild008()
    {
        LinearExpression expression = new LinearExpressionBuilder().build(PI.multiply(number(42)));
        assertEquals("Linear[42/1 {pi}]", expression.toString());
    }

    @Test
    public void testBuild009()
    {
        LinearExpression expression = new LinearExpressionBuilder().build(Value.ONE.divide(number(2)));
        assertEquals("Linear[1/2 {1}]", expression.toString());
    }

    @Test
    public void testBuild010()
    {
        LinearExpression expression = new LinearExpressionBuilder().build(PI.add(E).divide(number(3)));
        assertEquals("Linear[1/3 {pi} + 1/3 {e}]", expression.toString());
    }

    @Test
    public void testBuild011()
    {
        LinearExpression expression = new LinearExpressionBuilder().build(number(320).divide(number(13).multiply(number(40))));
        assertEquals("Linear[8/13 {1}]", expression.toString());
    }

    @Test
    public void testBuild012()
    {
        Value input = number(72000).add(number(-8000).multiply(number(11).sqrt())).divide(number(2000));
        LinearExpression expression = new LinearExpressionBuilder().build(input);
        assertEquals("Linear[36/1 {1} + -4/1 {sqrt(11)}]", expression.toString());
    }

    @Test
    public void testBuild013()
    {
        Value input = number(2).add(fraction(21, 37).add(fraction(5, 37).multiply(number(287).sqrt()))).divide(number(10));
        LinearExpression expression = new LinearExpressionBuilder().build(input);
        assertEquals("Linear[19/74 {1} + 1/74 {sqrt(287)}]", expression.toString());
    }

    @Test
    public void testBuild014()
    {
        Value input = number(10).multiply(number(2).add(number(-15).divide(number(74)).add(number(7).divide(number(74)).multiply(number(287).sqrt()))));
        LinearExpression expression = new LinearExpressionBuilder().build(input);
        assertEquals("Linear[665/37 {1} + 35/37 {sqrt(287)}]", expression.toString());
    }
}
