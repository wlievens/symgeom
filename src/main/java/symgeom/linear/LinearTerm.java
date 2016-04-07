package symgeom.linear;

import lombok.RequiredArgsConstructor;
import symgeom.util.Util;
import symgeom.value.Value;

@lombok.Value
@RequiredArgsConstructor
public class LinearTerm
{
    private int numerator;
    private int denominator;
    private Value value;

    @Override
    public String toString()
    {
        return String.format("%d/%d {%s}", numerator, denominator, value);
    }

    public LinearTerm simplify()
    {
        int gcd = Util.gcd(this.numerator, this.denominator);
        int numerator = this.numerator / gcd;
        int denominator = this.denominator / gcd;
        if (denominator < 0)
        {
            return create(-numerator, -denominator, value);
        }
        return create(numerator, denominator, value);
    }

    public static LinearTerm create(Value value)
    {
        return create(1, 1, value);
    }

    public static LinearTerm create(int numerator, int denominator, Value value)
    {
        return new LinearTerm(numerator, denominator, value);
    }

    public static LinearTerm create(int numerator, int denominator)
    {
        return new LinearTerm(numerator, denominator, Value.ONE);
    }
}
