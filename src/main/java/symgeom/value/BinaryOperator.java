package symgeom.value;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.BiFunction;

@Getter
@RequiredArgsConstructor
public enum BinaryOperator
{
    ADD("+", 2, false, true, (x, y) -> x + y),
    SUBTRACT("-", 2, false, false, (x, y) -> x - y),
    MULTIPLY("*", 3, false, true, (x, y) -> x * y),
    DIVIDE("/", 3, false, false, (x, y) -> x / y),
    MODULO("%", 3, false, false, (x, y) -> x % y),
    POWER("^", 4, false, false, (x, y) -> Math.pow(x, y)),
    MIN("min", 0, true, true, Math::min),
    MAX("max", 0, true, true, Math::max);

    private final String symbol;
    private final int precedence;
    private final boolean applicative;
    private final boolean commutative;
    private final BiFunction<Double, Double, Double> approximator;
}
