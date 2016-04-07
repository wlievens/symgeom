package symgeom.value;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public final class CosineValue extends AbstractUnaryValue
{
    private CosineValue(Value operand)
    {
        super(operand);
    }

    public Value simplify()
    {
        Value operand = getOperand().simplify();
        if (operand == PI)
        {
            return Value.number(-1);
        }
        if (operand.isInteger() && operand.asInteger() == 0)
        {
            return Value.ONE;
        }
        return create(operand);
    }

    @Override
    public UnaryOperator getOperator()
    {
        return UnaryOperator.COSINE;
    }

    public static Value create(Value operand)
    {
        return new CosineValue(operand);
    }
}
