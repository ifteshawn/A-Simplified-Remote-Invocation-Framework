/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Contract;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Ifte
 */
public class CalculatePrime implements Task, Serializable {

    private static final long serialVersionUID = 2345632L;
    
    //this is the integer number that the user enters to find out the prime numbers upto this number
    private final int NUMBER;
    
    //to store the number of primes
    private int numOfPrimes;
    
    //to store the prime numbers realized from the computation
    private final ArrayList<Integer> primes;
    
    //constructor taking the number and arraylist as input
    public CalculatePrime(int number) {
        this.NUMBER = number;
        primes = new ArrayList<>();
    }
    
    //algorithm to figure out if a number is prime or not iteratively
    public boolean isPrime(int number) {
        for (int i = 2; i < number; i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }
    
    //this is to increment the number of primes found everytime a number turns out to be prime
    public void incrementNumOfPrimes(){
        numOfPrimes++;
    }

    public int getNumOfPrimes() {
        return numOfPrimes;
    }
    
    public int getNUMBER() {
        return NUMBER;
    }
    
    public ArrayList<Integer> getPrimes() {
        return primes;
    }
    
    //This method does the computation and add the prime numbers to the list of primes.
    @Override
    public void executeTask() {
        for (int i = 2; i <= getNUMBER(); i++){
            if(isPrime(i)){
                incrementNumOfPrimes();
                primes.add(i);
            }
        }
    }

    @Override
    public Object getResult() {
        String res; 
        res = "Result: The number of primes is: " + getNumOfPrimes() + ", and they are: " 
                + getPrimes() + ".";
        return res;
    }

    @Override
    public String toString() {
        return "CalculatePrime{" + "NUMBER=" + NUMBER + ", numOfPrimes=" + numOfPrimes + ", primes=" + primes + '}';
    }
    
    
        
}
