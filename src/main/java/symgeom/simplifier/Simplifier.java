package symgeom.simplifier;

import symgeom.linear.LinearExpressionBuilder;
import symgeom.value.AbsValue;
import symgeom.value.AbstractBinaryValue;
import symgeom.value.AbstractConstantValue;
import symgeom.value.AbstractUnaryValue;
import symgeom.value.AddValue;
import symgeom.value.CosValue;
import symgeom.value.DivideValue;
import symgeom.value.IntegerValue;
import symgeom.value.MultiplyValue;
import symgeom.value.NegateValue;
import symgeom.value.PowerValue;
import symgeom.value.SignValue;
import symgeom.value.SinValue;
import symgeom.value.SubtractValue;
import symgeom.value.Value;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static symgeom.util.Util.isInt;
import static symgeom.value.Value.*;

public class Simplifier
{
    private final List<SimplifyRule> rules = new ArrayList<>();

    public Simplifier()
    {
        setupRules();
    }

    public Value simplify(Value value)
    {
        // Ignore atomics
        if (value instanceof AbstractConstantValue || value instanceof IntegerValue)
        {
            return value;
        }
        // Traverse Unaries
        Class<? extends Value> type = value.getClass();
        if (value instanceof AbstractUnaryValue)
        {
            Value operand = simplify(((AbstractUnaryValue)value).getOperand());
            value = createUnary(type, operand);
        }
        // Traverse Binaries
        if (value instanceof AbstractBinaryValue)
        {
            Value left = simplify(((AbstractBinaryValue)value).getLeft());
            Value right = simplify(((AbstractBinaryValue)value).getRight());
            value = createBinary(type, left, right);
        }
        // System.out.println("SIMPLIFY " + value.toPrefix());
        // Apply rules
        value = applyRules(value);
        // System.out.println("<-- " + value);
        return value;
    }

    private Value applyRules(Value value)
    {
        Value symmetric = null;
        if (value instanceof AbstractBinaryValue)
        {
            AbstractBinaryValue binary = (AbstractBinaryValue)value;
            if (binary.getOperator().isCommutative())
            {
                symmetric = createBinary(binary.getClass(), binary.getRight(), binary.getLeft());
            }
        }
        for (SimplifyRule rule : rules)
        {
            if (rule.match(value))
            {
                // System.out.println(rule.getLabel());
                Value result = rule.execute(value);
                if (result != null && !result.equals(value))
                {
                    return simplify(result);
                }
            }
            else if (symmetric != null && rule.match(symmetric))
            {
                // System.out.println(rule.getLabel());
                Value result = rule.execute(symmetric);
                if (result != null && !result.equals(symmetric))
                {
                    return simplify(result);
                }
            }
        }
        return value;
    }

    private Value createUnary(Class<? extends Value> type, Value operand)
    {
        try
        {
            Value value;
            Method create = type.getMethod("create", Value.class);
            value = (Value)create.invoke(null, operand);
            return value;
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            throw new IllegalStateException(e);
        }
    }

    private Value createBinary(Class<? extends Value> type, Value left, Value right)
    {
        try
        {
            Value value;
            Method create = type.getMethod("create", Value.class, Value.class);
            value = (Value)create.invoke(null, left, right);
            return value;
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            throw new IllegalStateException(e);
        }
    }

    private void setupRules()
    {
        // Neutral Addition
        rules.add(SimplifyBinaryRule.create(
                "A+0  ->  A",
                value -> value instanceof AddValue,
                (left, right) -> right.equals(ZERO),
                (left, right) -> left
        ));

        // Neutral Multiplication
        rules.add(SimplifyBinaryRule.create(
                "A*1  ->  A",
                value -> value instanceof MultiplyValue,
                (left, right) -> right.equals(ONE),
                (left, right) -> left
        ));

        // Neutral Exponentiation
        rules.add(SimplifyBinaryRule.create(
                "A^0  ->  1",
                value -> value instanceof PowerValue,
                (left, right) -> right.equals(ZERO),
                (left, right) -> ONE
        ));
        rules.add(SimplifyBinaryRule.create(
                "A^1  ->  A",
                value -> value instanceof PowerValue,
                (left, right) -> right.equals(ONE),
                (left, right) -> left
        ));

        // Division Identity
        rules.add(SimplifyBinaryRule.create(
                "A/A  ->  1",
                value -> value instanceof DivideValue,
                (left, right) -> left.equals(right),
                (left, right) -> ONE
        ));

        // Integer Addition
        rules.add(SimplifyBinaryRule.create(
                "A+B integers",
                value -> value instanceof AddValue,
                (left, right) -> left.isInteger() && right.isInteger(),
                (left, right) -> number(left.asInteger().add(right.asInteger()))
        ));

        // Integer Subtraction
        rules.add(SimplifyBinaryRule.create(
                "A-B integers",
                value -> value instanceof SubtractValue,
                (left, right) -> left.isInteger() && right.isInteger(),
                (left, right) -> number(left.asInteger().subtract(right.asInteger()))
        ));

        // Integer Multiplication
        rules.add(SimplifyBinaryRule.create(
                "A*B integers",
                value -> value instanceof MultiplyValue,
                (left, right) -> left.isInteger() && right.isInteger(),
                (left, right) -> number(left.asInteger().multiply(right.asInteger()))
        ));

        // Integer Division
        rules.add(SimplifyBinaryRule.create(
                "A/B integers using gcd",
                value -> value instanceof DivideValue,
                (left, right) -> left.isInteger() && right.isInteger(),
                (left, right) -> {
                    BigInteger a = left.asInteger();
                    BigInteger b = right.asInteger();
                    BigInteger gcd = a.gcd(b);
                    if (gcd.compareTo(BigInteger.ZERO) < -1)
                    {
                        gcd = gcd.negate();
                    }
                    if (gcd.equals(BigInteger.ONE))
                    {
                        return null;
                    }
                    BigInteger numerator = a.divide(gcd);
                    BigInteger denominator = b.divide(gcd);
                    if (denominator.equals(BigInteger.ONE))
                    {
                        return number(numerator);
                    }
                    return fraction(numerator, denominator);
                }
        ));

        // Integer Exponentiation
        rules.add(SimplifyBinaryRule.create(
                "A^B integers",
                value -> value instanceof PowerValue,
                (left, right) -> left.isInteger() && right.isInteger(),
                (left, right) -> {
                    BigInteger exponent = right.asInteger();
                    if (isInt(exponent))
                    {
                        return number(left.asInteger().pow(exponent.intValue()));
                    }
                    return null;
                }
        ));

        /// ...
        rules.add(SimplifyBinaryRule.create(
                "A*(B*C)  ->  AB*C  if AB integer",
                value -> value instanceof MultiplyValue,
                (left, right) -> right instanceof MultiplyValue && left.isInteger() && ((MultiplyValue)right).getLeft().isInteger(),
                (left, right) -> {
                    Value a = left;
                    Value b = ((MultiplyValue)right).getLeft();
                    Value c = ((MultiplyValue)right).getRight();
                    return number(a.asInteger().multiply(b.asInteger())).multiply(c);
                }
        ));

        rules.add(SimplifyBinaryRule.create(
                "(A+B)-C  ->  (A-C)+B  if A and C integer",
                value -> value instanceof SubtractValue,
                (left, right) -> left instanceof AddValue && ((AddValue)left).getLeft().isInteger() && right.isInteger(),
                (left, right) -> ((AddValue)left).getLeft().subtract(right).add(((AddValue)left).getRight())
        ));
        rules.add(SimplifyBinaryRule.create(
                "A*A  ->  A^2",
                value -> value instanceof MultiplyValue,
                (left, right) -> left.equals(right),
                (left, right) -> left.power(number(2))
        ));
        rules.add(SimplifyBinaryRule.create(
                "(A*B)/B  ->  A",
                value -> value instanceof DivideValue,
                (left, right) -> left instanceof MultiplyValue && ((MultiplyValue)left).getRight().equals(right),
                (left, right) -> ((MultiplyValue)left).getLeft()
        ));
        rules.add(SimplifyBinaryRule.create(
                "(A*B)/A  ->  B",
                value -> value instanceof DivideValue,
                (left, right) -> left instanceof MultiplyValue && ((MultiplyValue)left).getLeft().equals(right),
                (left, right) -> ((MultiplyValue)left).getRight()
        ));
        rules.add(SimplifyBinaryRule.create(
                "(A/B)*B  ->  A",
                value -> value instanceof MultiplyValue,
                (left, right) -> left instanceof DivideValue && ((DivideValue)left).getRight().equals(right),
                (left, right) -> ((DivideValue)left).getLeft()
        ));
        rules.add(SimplifyBinaryRule.create(
                "(A/B)*C  ->  (AC)/B  if A and C integer",
                value -> value instanceof MultiplyValue,
                (left, right) -> left instanceof DivideValue && ((DivideValue)left).getLeft().isInteger() && right.isInteger(),
                (left, right) -> ((DivideValue)left).getLeft().multiply(right).divide(((DivideValue)left).getRight())
        ));
        rules.add(SimplifyBinaryRule.create(
                "(A*B)/C  ->  (A/C)*B  if A and C integer",
                value -> value instanceof DivideValue,
                (left, right) -> left instanceof MultiplyValue && ((MultiplyValue)left).getLeft().isInteger() && right.isInteger(),
                (left, right) -> ((MultiplyValue)left).getLeft().divide(right).multiply(((MultiplyValue)left).getRight())
        ));
        rules.add(SimplifyBinaryRule.create(
                "(A*B)/C  ->  (B/C)*A  if B and C integer",
                value -> value instanceof DivideValue,
                (left, right) -> left instanceof MultiplyValue && ((MultiplyValue)left).getRight().isInteger() && right.isInteger(),
                (left, right) -> ((MultiplyValue)left).getRight().divide(right).multiply(((MultiplyValue)left).getLeft())
        ));
        rules.add(SimplifyBinaryRule.create(
                "A/(B/C)  ->  AC/B",
                value -> value instanceof DivideValue,
                (left, right) -> right instanceof DivideValue,
                (left, right) -> left.multiply(((DivideValue)right).getRight()).divide(((DivideValue)right).getLeft())
        ));
        rules.add(SimplifyBinaryRule.create(
                "(A/B)^C  ->  (A^C)/(B^C)  if A and B and C integer",
                value -> value instanceof PowerValue,
                (left, right) -> left.isFraction() && right.isInteger(),
                (left, right) -> ((DivideValue)left).getLeft().power(right).divide(((DivideValue)left).getRight().power(right))
        ));

        rules.add(SimplifyBinaryRule.create(
                "A*(-B)  ->  -A*B",
                value -> value instanceof MultiplyValue,
                (left, right) -> right instanceof NegateValue,
                (left, right) -> left.negate().multiply(((NegateValue)right).getOperand())
        ));
        rules.add(SimplifyBinaryRule.create(
                "A/(-B)  ->  -A/B",
                value -> value instanceof DivideValue,
                (left, right) -> right instanceof DivideValue,
                (left, right) -> left.negate().divide(((NegateValue)right).getOperand())
        ));
        rules.add(SimplifyBinaryRule.create(
                "(A^B)^C  ->  A^(BC)",
                value -> value instanceof PowerValue,
                (left, right) -> left instanceof PowerValue,
                (left, right) -> ((PowerValue)left).getLeft().power(((PowerValue)left).getRight().multiply(right))
        ));
        rules.add(SimplifyBinaryRule.create(
                "-1*A  ->  -A",
                value -> value instanceof MultiplyValue,
                (left, right) -> left.isInteger() && left.asInteger().equals(BigInteger.valueOf(-1)),
                (left, right) -> right.negate()
        ));
        rules.add(SimplifyBinaryRule.create(
                "(A*B)^C  ->  (A^C)*(B^C)  if A or B and C numeric",
                value -> value instanceof PowerValue,
                (left, right) -> left instanceof MultiplyValue
                        && right.isNumeric()
                        && (((MultiplyValue)left).getLeft().isNumeric() || ((MultiplyValue)left).getRight().isNumeric()),
                (left, right) -> ((MultiplyValue)left).getLeft().power(right).multiply(((MultiplyValue)left).getRight().power(right))
        ));
        rules.add(SimplifyBinaryRule.create(
                "0*A  ->  0",
                value -> value instanceof MultiplyValue,
                (left, right) -> left.equals(ZERO),
                (left, right) -> ZERO
        ));
        rules.add(SimplifyBinaryRule.create(
                "0^A  ->  0",
                value -> value instanceof PowerValue,
                (left, right) -> left.equals(ZERO),
                (left, right) -> ZERO
        ));
        rules.add(SimplifyBinaryRule.create(
                "Reduce number under square root",
                value -> value instanceof PowerValue,
                (left, right) -> left.isInteger() && left.asInteger().compareTo(BigInteger.ZERO) > 0 && right.equals(HALF),
                (left, right) -> {
                    BigInteger value = left.asInteger();
                    BigInteger outside = BigInteger.ONE;
                    BigInteger inside = BigInteger.ONE;
                    BigInteger number = value;
                    for (BigInteger base = BigInteger.valueOf(2); base.compareTo(number) < 0; base = base.add(BigInteger.ONE))
                    {
                        int exponent = 0;
                        while (number.mod(base).equals(BigInteger.ZERO))
                        {
                            number = number.divide(base);
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
                            inside = inside.multiply(base);
                        }
                        if (exp2 > 0)
                        {
                            outside = outside.multiply(base.pow(exp2));
                        }
                    }
                    inside = inside.multiply(number);
                    if (inside.equals(BigInteger.ONE))
                    {
                        return Value.number(outside);
                    }
                    if (outside.compareTo(BigInteger.ONE) > 0)
                    {
                        return Value.number(outside).multiply(Value.number(inside).sqrt());
                    }
                    return null;
                }
        ));

        rules.add(SimplifyUnaryRule.create(
                "cos(0)  ->  1",
                value -> value instanceof CosValue,
                operand -> operand.equals(ZERO),
                operand -> ONE
        ));
        rules.add(SimplifyUnaryRule.create(
                "cos(PI)  ->  -1",
                value -> value instanceof CosValue,
                operand -> operand.equals(PI),
                operand -> number(-1)
        ));
        rules.add(SimplifyUnaryRule.create(
                "sin(0)  ->  0",
                value -> value instanceof SinValue,
                operand -> operand.equals(ZERO),
                operand -> ZERO
        ));
        rules.add(SimplifyUnaryRule.create(
                "sin(PI)  ->  0",
                value -> value instanceof SinValue,
                operand -> operand.equals(PI),
                operand -> ZERO
        ));
        rules.add(SimplifyUnaryRule.create(
                "-(A)  ->  -A  if A integer",
                value -> value instanceof NegateValue,
                operand -> operand instanceof IntegerValue,
                operand -> number(operand.asInteger().negate())
        ));
        rules.add(SimplifyUnaryRule.create(
                "-(-A))  ->  A",
                value -> value instanceof NegateValue,
                operand -> operand instanceof NegateValue,
                operand -> ((NegateValue)operand).getOperand()
        ));
        rules.add(SimplifyUnaryRule.create(
                "-(A/B)  ->  (-A/B)",
                value -> value instanceof NegateValue,
                operand -> operand instanceof DivideValue,
                operand -> ((DivideValue)operand).getLeft().negate().divide(((DivideValue)operand).getRight())
        ));
        rules.add(SimplifyUnaryRule.create(
                "sign(A)  ->  sign if known",
                value -> value instanceof SignValue,
                operand -> operand.getSign().isKnown(),
                operand -> number(operand.getSign().toInteger())
        ));
        rules.add(SimplifyUnaryRule.create(
                "abs(0)  ->  0",
                value -> value instanceof AbsValue,
                operand -> operand.isZero().isTrue(),
                operand -> ZERO
        ));
        rules.add(SimplifyUnaryRule.create(
                "abs(A)  ->  A  if A positive",
                value -> value instanceof AbsValue,
                operand -> operand.isPositive().isTrue(),
                operand -> operand
        ));
        rules.add(SimplifyUnaryRule.create(
                "abs(A)  ->  -A  if A negative",
                value -> value instanceof AbsValue,
                operand -> operand.isNegative().isTrue(),
                operand -> operand.negate()
        ));

        // Linear Expression after all other special forms
        LinearExpressionBuilder builder = new LinearExpressionBuilder();
        rules.add(SimplifyRule.create(
                "Linear Expression",
                value -> !value.isInteger(),
                value -> builder.build(value).toValue()
        ));
    }
}
