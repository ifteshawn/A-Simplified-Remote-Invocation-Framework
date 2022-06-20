/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Contract;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Ifte
 */
public class CalculatePi implements Task, Serializable {
    
    private static final long serialVersionUID = 227L;
    /**
     * constants used in pi computation
     */
    private static final BigDecimal FOUR = BigDecimal.valueOf(4);
    /**
     * rounding mode to use during pi computation
     */
    private static final int ROUNDINGMODE = BigDecimal.ROUND_HALF_EVEN;
    /**
     * digits of precision after the decimal point
     */
    private final int digits;
    
    //BigDecimal type variable to store the result after computation.
    private BigDecimal answer;

    /**
     * Construct a task to calculate pi to the specified precision.
     */
    public CalculatePi(int digits) {
        this.digits = digits;
    }

    /**
     * Calculate pi.
     */
    public BigDecimal execute() {
        return computePi(digits);
    }

    /**
     * Compute the value of pi to the specified number of * digits after the
     * decimal point. The value is * computed using Machin's formula: * pi/4 =
     * 4*arctan(1/5) - arctan(1/239) * and a power series expansion of arctan(x)
     * to * sufficient precision.
     */
    public static BigDecimal computePi(int digits) {
        int scale = digits + 5;
        BigDecimal arctan1_5 = arctan(5, scale);
        BigDecimal arctan1_239 = arctan(239, scale);
        BigDecimal pi = arctan1_5.multiply(FOUR).subtract(arctan1_239).multiply(FOUR);
        return pi.setScale(digits, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Compute the value, in radians, of the arctangent of * the inverse of the
     * supplied integer to the specified number of digits after the decimal
     * point. The value is computed using the power series expansion for the *
     * arc tangent: * arctan(x) = x - (x^3)/3 + (x^5)/5 - (x^7)/7 +(x^9)/9 ...
     */
    public static BigDecimal arctan(int inverseX, int scale) {
        BigDecimal result, numer, term;
        BigDecimal invX = BigDecimal.valueOf(inverseX);
        BigDecimal invX2 = BigDecimal.valueOf(inverseX * inverseX);
        numer = BigDecimal.ONE.divide(invX, scale, ROUNDINGMODE);
        result = numer;
        int i = 1;
        do {
            numer = numer.divide(invX2, scale, ROUNDINGMODE);
            int denom = 2 * i + 1;
            term = numer.divide(BigDecimal.valueOf(denom), scale, ROUNDINGMODE);
            if ((i % 2) != 0) {
                result = result.subtract(term);
            } else {
                result = result.add(term);
            }
            i++;
        } while (term.compareTo(BigDecimal.ZERO) != 0);
        return result;
    }
    
    public void setAnswer(BigDecimal result) {
        this.answer = result;
    }

    public BigDecimal getAnswer() {
        return answer;
    }
    
    @Override
    public void executeTask() {
        setAnswer(execute());
    }

    @Override
    public Object getResult() {
        String result = "Result: " + getAnswer() + ".";
        return result;
    }
    
}
