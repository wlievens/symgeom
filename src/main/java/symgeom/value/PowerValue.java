package symgeom.value;

import lombok.EqualsAndHashCode;
import symgeom.util.Util;

import java.math.BigInteger;

@EqualsAndHashCode(callSuper = true)
public final class PowerValue extends AbstractBinaryValue
{
    private PowerValue(Value left, Value right)
    {
        super(left, right);
    }

    @Override
    public String toExpression(int precedence)
    {
        if (getRight().eq(fraction(1, 2)).isTrue())
        {
            return "sqrt(" + getLeft().toString() + ")";
        }
        return super.toExpression(precedence);
    }

    public Value simplify()
    {
        Value left = getLeft().simplify();
        if (left.isZero().isTrue())
        {
            return ZERO;
        }
        if (left.isOne().isTrue())
        {
            return ONE;
        }
        Value right = getRight().simplify();
        if (right.isZero().isTrue())
        {
            return ONE;
        }
        boolean leftInt = left.isInteger();
        boolean rightInt = right.isInteger();
        if (rightInt)
        {
            if (right.asInteger() == 0)
            {
                return number(1);
            }
            if (right.asInteger() == 1)
            {
                return left;
            }
            if (left.isStrictlyNegative().isTrue() && right.isEven().isTrue())
            {
                return create(left.abs().simplify(), right).simplify();
            }
        }
        if (leftInt && rightInt)
        {
            BigInteger bigResult = BigInteger.valueOf(left.asInteger()).pow(right.asInteger());
            if (Util.isInt(bigResult))
            {
                return Value.number(bigResult.intValue());
            }
        }
        if (left instanceof PowerValue)
        {
            // (a ^ b) ^ c --> a ^ bc
            PowerValue power = (PowerValue)left;
            Value a = power.getLeft();
            Value b = power.getRight();
            Value c = right;
            Value bc = b.multiply(c).simplify();
            return create(a, bc).simplify();
        }
        if (left.isInteger() && right.eq(fraction(1, 2)).isTrue())
        {
            int value = left.asInteger();
            if (value > 0)
            {
                int outside = 1;
                int inside = 1;
                int number = value;
                for (int base = 2; base <= (number); base++)
                {
                    int exponent = 0;
                    while (number % base == 0)
                    {
                        number /= base;
                        exponent++;
                    }
                    if (exponent == 0)
                    {
                        continue;
                    }
                    int exp2 = exponent / 2;
                    int expIn = exponent % 2;
                    if (expIn == 1)
                    {
                        inside *= base;
                    }
                    if (exp2 > 0)
                    {
                        outside *= BigInteger.valueOf(base).pow(exp2).intValue();
                    }
                }
                if (inside == 1)
                {
                    return Value.number(outside);
                }
                if (outside > 1)
                {
                    return Value.number(outside).multiply(Value.number(inside).sqrt());
                }
            }
        }

        if (left instanceof MultiplyValue)
        {
            // (A*B)^C --> A^C*B^C  [if simpler]
            Value a = ((MultiplyValue)left).getLeft().simplify();
            Value b = ((MultiplyValue)left).getRight().simplify();
            Value c = right;
            Value result = a.power(c).multiply(b.power(c)).simplify();
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
        // A^B < C
        Value a = getLeft().simplify();
        Value b = getRight().simplify();
        Value c = value.simplify();
        Sign cSign = c.getSign();
        if (cSign.isZero())
        {
            if (b.eq(fraction(1, 2)).isTrue())
            {
                // SQRT(A) < 0  ->  [false if A > 0]
                return a.gt(ZERO).invert();
            }
        }
        if (cSign.isNegative())
        {
            if (b.eq(fraction(1, 2)).isTrue())
            {
                // SQRT is never negative
                return Tribool.FALSE;
            }
        }
        if (b instanceof DivideValue)
        {
            // A^(D/E) < C
            DivideValue divide = (DivideValue)b;
            Value d = divide.getLeft();
            Value e = divide.getRight();
            if (cSign.isPositive() && d.eq(Value.ONE).isTrue())
            {
                // A^(1/E) < C  ->  A < C^E
                Value ce = c.power(e).simplify();
                return a.lt(ce);
            }
        }
        return super.lt(c);
    }

    @Override
    public BinaryOperator getOperator()
    {
        return BinaryOperator.POWER;
    }

    public static Value create(Value left, Value right)
    {
        return new PowerValue(left, right);
    }
}
