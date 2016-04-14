package symgeom.value;

import symgeom.simplifier.Simplifier;

public abstract class Value
{
    public static final Value PI = PIValue.INSTANCE;
    public static final Value E = EValue.INSTANCE;
    public static final Value ZERO = Value.number(0);
    public static final Value ONE = Value.number(1);

    private static final Simplifier simplifier = new Simplifier();

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
        value = value.simplify();
        if (equals(value))
        {
            return Tribool.TRUE;
        }
        return simplify().eqInternal(value);
    }

    public abstract Tribool eqInternal(Value value);

    public abstract Tribool lt(Value value);

    public final Tribool gt(Value value)
    {
        // Do not replace this with 'lt(value).invert()' as that is not the same for the ZERO case!
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

    public final boolean isFraction()
    {
        return this instanceof DivideValue && ((DivideValue)this).getLeft().isInteger() && ((DivideValue)this).getRight().isInteger();
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
        return IntegerValue.create(value);
    }

    public static final Value add(Value left, Value right)
    {
        return AddValue.create(left, right);
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
