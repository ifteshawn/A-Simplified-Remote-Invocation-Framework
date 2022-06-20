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
public class CSMessage implements Task, Serializable {
    //The variable that holds the error information
    private String finalResult;
    
    public CSMessage() { 
    }
    
    //Set the error message
    public void setMessage(String msg) {
        finalResult = msg;
    }
        
    @Override
    public void executeTask() {
    
    }
    
    //Return the error message
    @Override
    public Object getResult() {
        return finalResult;
    }
    
}

