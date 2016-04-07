package symgeom.value;

import lombok.EqualsAndHashCode;
import symgeom.util.Util;

@EqualsAndHashCode(callSuper = true)
public final class DivideValue extends AbstractBinaryValue
{
    private DivideValue(Value left, Value right)
    {
        super(left, right);
    }

    public Value simplify()
    {
        Value left = getLeft().simplify();
        Value right = getRight().simplify();
        if (left.isInteger() && right.isInteger())
        {
            int num = left.asInteger();
            int den = right.asInteger();
            if (num % den == 0)
            {
                return Value.number(num / den);
            }
            int gcd = Util.gcd(num, den);
            if (gcd != 1)
            {
                if (den / gcd < 0)
                {
                    gcd = -gcd;
                }
                return fraction(num / gcd, den / gcd);
            }
        }

        if (left instanceof DivideValue)
        {
            DivideValue divide = (DivideValue)left;
            return create(divide.getLeft(), divide.getRight().multiply(right));
        }

        if (right.isStrictlyNegative().isTrue())
        {
            return create(left.negate().simplify(), right.negate().simplify());
        }

        if (left instanceof AddValue)
        {
            // (A+B)/C -> try to reduce for gcd

            Value a = ((AddValue)left).getLeft();
            Value b = ((AddValue)left).getRight();
            Value c = right;

            if (c.isInteger())
            {
                System.out.println(a.toPrefix());
                System.out.println(b.toPrefix());
                System.out.println(c.toPrefix());

                int cnum = c.asInteger();
                for (int divisor = cnum / 2; divisor >= 2; divisor--)
                {
                    if (cnum % divisor == 0)
                    {
                        Value adiv = a.divide(Value.number(divisor));
                        if (adiv.getDepth() > a.getDepth())
                        {
                            continue;
                        }
                        Value bdiv = b.divide(Value.number(divisor));
                        if (bdiv.getDepth() > b.getDepth())
                        {
                            continue;
                        }
                        return create(adiv.add(bdiv), Value.number(cnum / divisor));
                    }
                }
            }
        }

        if (left instanceof MultiplyValue && false)
        {
            // (A*B)/C -> (A/C)*B  (if simpler)
            Value a = ((MultiplyValue)left).getLeft();
            Value b = ((MultiplyValue)left).getRight();
            Value c = right;
            Value result = a.divide(c).multiply(b);
            if (result.getDepth() < getDepth())
            {
                return result;
            }
        }

        return create(left, right);
    }

    @Override
    public Tribool lt(Value value)
    {
        return getLeft().lt(value.multiply(getRight()));
    }

    @Override
    public BinaryOperator getOperator()
    {
        return BinaryOperator.DIVIDE;
    }

    public static Value create(Value numerator, Value denominator)
    {
        return new DivideValue(numerator, denominator);
    }

}
