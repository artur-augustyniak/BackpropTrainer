/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.aaugustyniak.backproptrainer.exceptions;

/**
 *
 * @author artur
 */
public class DefinitionFileException extends Exception{

    public DefinitionFileException(String currentAnnDef, String file) {
        System.err.println("Ann def. " + currentAnnDef);
        System.err.println("tcase file" + file);
    }
    
}
