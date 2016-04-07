package symgeom.linear;

import org.junit.Test;
import symgeom.value.Value;

import static org.junit.Assert.assertEquals;

public class TestLinearExpression
{
    @Test
    public void testToString()
    {
        LinearExpression expression = LinearExpression.create(
            LinearTerm.create(2, 3, Value.PI),
            LinearTerm.create(15, 7, Value.E)
        );
        assertEquals("Linear[(2 / 3 * pi) + (15 / 7 * e)]", expression.toString());
    }

    @Test
    public void testSimplify001()
    {
        LinearExpression expression = LinearExpression.create(
            LinearTerm.create(4, 6, Value.PI),
            LinearTerm.create(200, 20, Value.E)
        );
        expression = expression.simplify();
        assertEquals("Linear[(2 / 3 * pi) + (10 * e)]", expression.toString());
    }

    @Test
    public void testSimplify002()
    {
        LinearExpression expression = LinearExpression.create(
            LinearTerm.create(3, 4, Value.PI),
            LinearTerm.create(14, 8, Value.PI),
            LinearTerm.create(19, 12, Value.PI),
            LinearTerm.create(5, 4, Value.E)
        );
        expression = expression.simplify();
        assertEquals("Linear[(49 / 12 * pi) + (5 / 4 * e)]", expression.toString());
    }

    @Test
    public void testSimplify003()
    {
        LinearExpression expression = LinearExpression.create(
            LinearTerm.create(-3, -7, Value.PI)
        );
        expression = expression.simplify();
        assertEquals("Linear[(3 / 7 * pi)]", expression.toString());
    }
}
