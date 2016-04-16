package symgeom.value;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public final class SubtractValue extends AbstractBinaryValue
{
    private SubtractValue(Value left, Value right)
    {
        super(left, right);
    }

    @Override
    public Tribool lt(Value value)
    {
        System.out.println(this + " < " + value);
        Value a = getLeft().simplify();
        Value b = getRight().simplify();
        Value c = value.simplify();
        if (b.isInteger())
        {
            // A-B < C  ->  A < C+B
            return a.lt(c.add(b).simplify());
        }
        return super.lt(value);
    }

    @Override
    public BinaryOperator getOperator()
    {
        return BinaryOperator.SUBTRACT;
    }

    public static Value create(Value left, Value right)
    {
        return new SubtractValue(left, right);
    }
}
