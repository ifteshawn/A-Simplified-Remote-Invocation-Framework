/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Contract;

import java.io.Serializable;

/**
 *
 * @author Ifte
 */
public class CalculateGCD implements Task, Serializable {
    private static final long serialVersionUID = 2345532L;

    // Variable to hold the computation result
    private long answer;
    
    private final long a;
    
    private final long b;
    
    public CalculateGCD(long a, long b) {
        this.a = a;
        this.b = b;
    }
    
    public long getA() {
        return a;
    }

    public long getB() {
        return b;
    }

    @Override
    public String toString() {
        return "CalculateGCD{" + "answer=" + getAnswer() + ", a=" + a + ", b=" + b + '}';
    }
    
    //Algorithm to do the computation
    public long CalculateGCD(long a, long b) {
        if (a == 0) {
            return b;
        } else {
            while (b != 0) {
                if (a > b) {
                    a = a - b;
                } else {
                    b = b - a;
                }
            }
            return a;
        }
    }
    
    private long getAnswer() {
        return answer;
    }
    
    public void setAnswer(long result) {
        this.answer = result;
    }
    
    //this set the answer variable after calculating the result
    @Override
    public void executeTask() {
        setAnswer(CalculateGCD(getA(), getB()));
    }
    
    @Override
    public Object getResult() {
        String result = "Result: " + getAnswer() + ".";
        return result;
    }
    
}
