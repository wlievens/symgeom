package symgeom.value;

import lombok.EqualsAndHashCode;
import symgeom.linear.LinearExpression;
import symgeom.linear.LinearExpressionBuilder;
import symgeom.util.Util;

@EqualsAndHashCode(callSuper = true)
public final class DivideValue extends AbstractBinaryValue
{
    private DivideValue(Value left, Value right)
    {
        super(left, right);
    }

    public Value old_simplify()
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

        if (left.eq(right).isTrue())
        {
            return ONE;
        }

        if (left instanceof DivideValue)
        {
            // (A/B)/C -> A/(B*C)
            Value a = ((DivideValue)left).getLeft();
            Value b = ((DivideValue)left).getRight();
            Value c = right;
            return create(a, b.multiply(c)).simplify();
        }

        if (right instanceof DivideValue)
        {
            // A/(B/C) -> (A*C)/B
            Value a = left;
            Value b = ((DivideValue)right).getLeft();
            Value c = ((DivideValue)right).getRight();
            return create(a.multiply(c), b).simplify();
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
                int cnum = c.asInteger();
                for (int divisor = cnum / 2; divisor >= 2; divisor--)
                {
                    if (cnum % divisor == 0)
                    {
                        Value adiv = a.divide(Value.number(divisor)).simplify();
                        if (adiv.getDepth() > a.getDepth())
                        {
                            continue;
                        }
                        Value bdiv = b.divide(Value.number(divisor)).simplify();
                        if (bdiv.getDepth() > b.getDepth())
                        {
                            continue;
                        }
                        return create(adiv.add(bdiv), Value.number(cnum / divisor));
                    }
                }
            }
        }

        if (left.isInteger() && right.isInteger())
        {
            return create(left, right);
        }

        LinearExpression expression = new LinearExpressionBuilder().build(this);
        return expression.toValue();
    }

    @Override
    public Tribool lt(Value value)
    {
        // A/B < C -> A <?> BC
        Value a = getLeft().simplify();
        Value b = getRight().simplify();
        Value c = value;
        Value bc = b.multiply(c).simplify();
        Sign sign = b.getSign();
        if (sign.isPositive())
        {
            return a.lt(bc);
        }
        if (sign.isNegative())
        {
            return bc.lt(a);
        }
        return super.lt(value);
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
