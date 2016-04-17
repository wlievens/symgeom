package symgeom.value;

import symgeom.comparator.Comparator;
import symgeom.comparator.Order;
import symgeom.simplifier.Simplifier;

import java.math.BigInteger;

public abstract class Value
{
    public static final Value PI = PIValue.INSTANCE;
    public static final Value E = EValue.INSTANCE;
    public static final Value ZERO = number(0);
    public static final Value ONE = number(1);
    public static final Value HALF = fraction(1, 2);

    private static final Simplifier simplifier = new Simplifier();
    private static final Comparator comparator = new Comparator();

    public final String toString()
    {
        if (false)
        {
            return toPrefix();
        }
        if (true)
        {
            return toExpression(0);
        }
        return toExpression(0) + " {" + approximate() + "}";
    }

    public abstract String toPrefix();

    public final Value divide(Value value)
    {
        return DivideValue.create(this, value);
    }

    public final Value modulo(Value value)
    {
        return ModuloValue.create(this, value);
    }

    public final Value abs()
    {
        return AbsValue.create(this);
    }

    public final Value negate()
    {
        return NegateValue.create(this);
    }

    public final Value square()
    {
        return PowerValue.create(this, number(2));
    }

    public final Value sqrt()
    {
        return PowerValue.create(this, HALF);
    }

    public final Value add(Value value)
    {
        return AddValue.create(this, value);
    }

    public final Value subtract(Value value)
    {
        return SubtractValue.create(this, value);
    }

    public final Value power(Value value)
    {
        return PowerValue.create(this, value);
    }

    public final Value multiply(Value value)
    {
        return MultiplyValue.create(this, value);
    }

    public final Value sign()
    {
        return SignValue.create(this);
    }

    public abstract String toExpression(int precedence);

    public final Value simplify()
    {
        return simplifier.simplify(this);
    }

    public abstract double approximate();

    public final Tribool eq(Value value)
    {
        Value left = this.simplify();
        Value right = value.simplify();
        Order order = comparator.compare(left, right);
        switch (order)
        {
            case EQUAL:
            {
                return Tribool.TRUE;
            }
            case LESSER:
            case GREATER:
            {
                return Tribool.FALSE;
            }
            case UNKNOWN:
            {
                return Tribool.UNKNOWN;
            }
            default:
            {
                throw new IllegalStateException();
            }
        }
    }

    public final Tribool lt(Value value)
    {
        Value left = this.simplify();
        Value right = value.simplify();
        Order order = comparator.compare(left, right);
        switch (order)
        {
            case LESSER:
            {
                return Tribool.TRUE;
            }
            case EQUAL:
            case GREATER:
            {
                return Tribool.FALSE;
            }
            case UNKNOWN:
            {
                return Tribool.UNKNOWN;
            }
            default:
            {
                throw new IllegalStateException();
            }
        }
    }

    public final Tribool gt(Value value)
    {
        Value left = this.simplify();
        Value right = value.simplify();
        Order order = comparator.compare(left, right);
        switch (order)
        {
            case GREATER:
            {
                return Tribool.TRUE;
            }
            case EQUAL:
            case LESSER:
            {
                return Tribool.FALSE;
            }
            case UNKNOWN:
            {
                return Tribool.UNKNOWN;
            }
            default:
            {
                throw new IllegalStateException();
            }
        }
    }

    public final Tribool isNegative()
    {
        return lt(ZERO);
    }

    public final Tribool isPositive()
    {
        return gt(ZERO);
    }

    public final Tribool isEven()
    {
        return modulo(Value.number(2)).eq(ZERO);
    }

    public final Tribool isOdd()
    {
        return isEven().invert();
    }

    public final Tribool between(Value low, Value high)
    {
        Tribool lower = lt(low);
        Tribool higher = gt(high);
        if (lower.isUnknown() || higher.isUnknown())
        {
            return Tribool.UNKNOWN;
        }
        return Tribool.of(lower.isFalse() && higher.isFalse());
    }

    public final Tribool lteq(Value radius)
    {
        return gt(radius).invert();
    }

    public final Tribool gteq(Value radius)
    {
        return lt(radius).invert();
    }

    public final boolean isNumeric()
    {
        return isInteger() || isFraction();
    }

    public final boolean isFraction()
    {
        return this instanceof DivideValue && ((DivideValue)this).getLeft().isInteger() && ((DivideValue)this).getRight().isInteger();
    }

    public final Sign getSign()
    {
        Tribool b;
        b = isNegative();
        if (b.isUnknown())
        {
            return Sign.UNKNOWN;
        }
        if (b.isTrue())
        {
            return Sign.NEGATIVE;
        }
        b = isPositive();
        if (b.isUnknown())
        {
            return Sign.UNKNOWN;
        }
        if (b.isTrue())
        {
            return Sign.POSITIVE;
        }
        return Sign.ZERO;
    }

    public Tribool isZero()
    {
        return eq(ZERO);
    }

    public abstract int getDepth();

    public final boolean isInteger()
    {
        return this instanceof IntegerValue;
    }

    public abstract boolean isAtom();

    public final BigInteger asInteger()
    {
        return ((IntegerValue)this).getValue();
    }

    public final Tribool isOne()
    {
        return eq(ONE);
    }

    public static Value min(Value left, Value right)
    {
        return MinValue.create(left, right);
    }

    public static Value max(Value left, Value right)
    {
        return MinValue.create(left, right);
    }

    public static Value cos(Value value)
    {
        return CosValue.create(value);
    }

    public static Value sin(Value value)
    {
        return SinValue.create(value);
    }

    public static Value number(int value)
    {
        return number(BigInteger.valueOf(value));
    }

    public static Value number(long value)
    {
        return number(BigInteger.valueOf(value));
    }

    public static Value number(BigInteger value)
    {
        return IntegerValue.create(value);
    }

    public static final Value add(Value left, Value right)
    {
        return AddValue.create(left, right);
    }

    public static Value fraction(BigInteger numerator, BigInteger denominator)
    {
        return DivideValue.create(number(numerator), number(denominator));
    }

    public static Value fraction(int numerator, int denominator)
    {
        return fraction(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
    }

    public static Value sqrt(Value value)
    {
        return value.sqrt();
    }
}
