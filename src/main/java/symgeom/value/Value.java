package symgeom.value;

public abstract class Value
{
    public static final Value PI = PIValue.INSTANCE;
    public static final Value E = PIValue.INSTANCE;
    public static final Value ZERO = Value.number(0);
    public static final Value ONE = Value.number(1);

    public final String toString()
    {
        return toExpression(0);
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
        return PowerValue.create(this, fraction(1, 2));
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

    public abstract String toExpression(int precedence);

    public abstract Value simplify();

    public abstract double approximate();

    public abstract Tribool eq(Value value);

    public abstract Tribool lt(Value value);

    public final Tribool gt(Value value)
    {
        return value.lt(this);
    }

    public final Tribool isStrictlyNegative()
    {
        return lt(ZERO);
    }

    public final Tribool isStrictlyPositive()
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

    public final Sign getSign()
    {
        Tribool b;
        b = isStrictlyNegative();
        if (b.isUnknown())
        {
            return Sign.UNKNOWN;
        }
        if (b.isTrue())
        {
            return Sign.NEGATIVE;
        }
        b = isStrictlyPositive();
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

    public final int asInteger()
    {
        return ((IntegerValue)this).getValue();
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
        return CosineValue.create(value);
    }

    public static Value sin(Value value)
    {
        return SineValue.create(value);
    }

    public static Value number(int value)
    {
        return IntegerValue.create(value);
    }

    public static Value fraction(int numerator, int denominator)
    {
        return DivideValue.create(number(numerator), number(denominator));
    }

    public static Value sqrt(Value value)
    {
        return PowerValue.create(value, fraction(1, 2));
    }
}
