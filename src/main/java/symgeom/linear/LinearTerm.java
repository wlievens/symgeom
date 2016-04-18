package symgeom.linear;

import lombok.RequiredArgsConstructor;
import symgeom.value.DivideValue;
import symgeom.value.NegateValue;
import symgeom.value.Value;

import java.math.BigInteger;

@lombok.Value
@RequiredArgsConstructor
public class LinearTerm
{
    private BigInteger numerator;
    private BigInteger denominator;
    private Value value;

    @Override
    public String toString()
    {
        return String.format("%d/%d {%s}", numerator, denominator, value);
    }

    public LinearTerm simplify()
    {
        if (value instanceof NegateValue && !isUnitary())
        {
            return create(numerator.negate(), denominator, ((NegateValue)value).getOperand()).simplify();
        }
        BigInteger gcd = this.numerator.gcd(this.denominator);
        if (gcd.equals(BigInteger.ONE) && denominator.compareTo(BigInteger.ZERO) > 0)
        {
            return this;
        }
        BigInteger numerator = this.numerator.divide(gcd);
        BigInteger denominator = this.denominator.divide(gcd);
        if (denominator.compareTo(BigInteger.ZERO) < 0)
        {
            return create(numerator.negate(), denominator.negate(), value);
        }
        return create(numerator, denominator, value);
    }

    public boolean isUnitary()
    {
        return numerator.equals(BigInteger.ONE) && denominator.equals(BigInteger.ONE);
    }

    public Value toValue()
    {
        Value numerator = Value.number(this.numerator);
        boolean numeratorOne = this.numerator.equals(BigInteger.ONE);
        boolean denominatorOne = this.denominator.equals(BigInteger.ONE);
        if (denominatorOne)
        {
            if (numeratorOne)
            {
                return value;
            }
            if (this.numerator.equals(BigInteger.valueOf(-1)))
            {
                return value.negate();
            }
            if (value.equals(Value.ONE))
            {
                return numerator;
            }
            if (value instanceof DivideValue)
            {
                DivideValue divide = (DivideValue)value;
                if (divide.getLeft().equals(Value.ONE))
                {
                    return numerator.divide(divide.getRight());
                }
            }
            return numerator.multiply(value);
        }
        Value denominator = Value.number(this.denominator);
        if (numeratorOne)
        {
            return value.divide(denominator);
        }
        if (value.equals(Value.ONE))
        {
            return numerator.divide(denominator);
        }
        return numerator.divide(denominator).multiply(value);
    }

    public LinearTerm multiply(BigInteger numerator, BigInteger denominator)
    {
        return create(this.numerator.multiply(numerator), this.denominator.multiply(denominator), value).simplify();
    }

    public static LinearTerm create(Value value)
    {
        return create(BigInteger.ONE, BigInteger.ONE, value);
    }

    public static LinearTerm create(int numerator, int denominator, Value value)
    {
        return create(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator), value);
    }

    public static LinearTerm create(int numerator, int denominator)
    {
        return create(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
    }

    public static LinearTerm create(BigInteger numerator, BigInteger denominator, Value value)
    {
        return new LinearTerm(numerator, denominator, value).simplify();
    }

    public static LinearTerm create(BigInteger numerator, BigInteger denominator)
    {
        return create(numerator, denominator, Value.ONE);
    }
}
