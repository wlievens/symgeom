package symgeom.simplifier;

import symgeom.linear.LinearExpressionBuilder;
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
        try
        {
            // Ignore atomics
            if (value instanceof AbstractConstantValue || value instanceof IntegerValue)
            {
                return value;
            }
            // Traverse Unaries
            if (value instanceof AbstractUnaryValue)
            {
                Value operand = simplify(((AbstractUnaryValue)value).getOperand());
                Method create = value.getClass().getMethod("create", Value.class);
                value = (Value)create.invoke(null, operand);
            }
            // Traverse Binaries
            if (value instanceof AbstractBinaryValue)
            {
                Value left = simplify(((AbstractBinaryValue)value).getLeft());
                Value right = simplify(((AbstractBinaryValue)value).getRight());
                Method create = value.getClass().getMethod("create", Value.class, Value.class);
                value = (Value)create.invoke(null, left, right);
            }
            System.out.println("SIMPLIFY " + value.toPrefix());
            // Apply rules
            for (int index = 0; index < rules.size(); index++)
            {
                SimplifyRule rule = rules.get(index);
                if (rule.match(value))
                {
                    // Apply the rule
                    Value result = rule.execute(value);
                    if (result != null)
                    {
                        System.out.println("::: " + rule.getLabel());
                        System.out.println("    value:  " + value.toPrefix());
                        System.out.println("    result: " + result.toPrefix());
                        if (!result.equals(value))
                        {
                            // Start simplification again
                            value = simplify(result);
                            break;
                        }
                    }
                }
            }
            System.out.println("<-- " + value);
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
        rules.add(SimplifyBinaryRule.create(
            "0+B  ->  B",
            value -> value instanceof AddValue,
            (left, right) -> left.equals(ZERO),
            (left, right) -> right
        ));

        // Neutral Multiplication
        rules.add(SimplifyBinaryRule.create(
            "A*1  ->  A",
            value -> value instanceof MultiplyValue,
            (left, right) -> right.equals(ONE),
            (left, right) -> left
        ));
        rules.add(SimplifyBinaryRule.create(
            "1*B  ->  B",
            value -> value instanceof MultiplyValue,
            (left, right) -> left.equals(ONE),
            (left, right) -> right
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
            "A*A  ->  1",
            value -> value instanceof DivideValue,
            (left, right) -> left.equals(right),
            (left, right) -> ONE
        ));
        rules.add(SimplifyBinaryRule.create(
            "1*B  ->  B",
            value -> value instanceof MultiplyValue,
            (left, right) -> left.equals(ONE),
            (left, right) -> right
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
                if (gcd.compareTo(BigInteger.ZERO) == -1)
                {
                    gcd = gcd.negate();
                }
                BigInteger numerator = a.divide(gcd);
                BigInteger denominator = b.divide(gcd);
                if (denominator.equals(BigInteger.ONE))
                {
                    return number(numerator);
                }
                if (gcd.equals(BigInteger.ONE))
                {
                    return null;
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
            "(A*B)*C  ->  BC*A  if BC integer",
            value -> value instanceof MultiplyValue,
            (left, right) -> left instanceof MultiplyValue && right.isInteger() && ((MultiplyValue)left).getRight().isInteger(),
            (left, right) -> {
                Value a = ((MultiplyValue)left).getLeft();
                Value b = ((MultiplyValue)left).getRight();
                Value c = right;
                return number(b.asInteger().multiply(c.asInteger())).multiply(a);
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

        // Linear Expression after all other special forms
        LinearExpressionBuilder builder = new LinearExpressionBuilder();
        rules.add(SimplifyRule.create(
            "Linear Expression",
            value -> true,
            value -> builder.build(value).toValue()
        ));
    }
}
