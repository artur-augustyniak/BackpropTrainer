/**
 * Copyright 2013 Artur Augustyniak
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package pl.aaugustyniak.backproptrainer.model;

import java.util.Random;
import org.neuroph.util.TransferFunctionType;
import pl.aaugustyniak.backproptrainer.arch.VarryActivationPerceptron.ActivationFunctionType;

/**
 * Entity for easy access to parsed network params
 *
 * @author Artur Augustyniak <artur@aaugustyniak.pl>
 */
public class NetworkParams {

    private Double beta;
    private Double slope;
    private Double slopePrim;
    private Double bias;
    private Integer rbfNum;
    private TransferFunctionType transfer;
    private ActivationFunctionType activation;
    
    /**
     * Randomize this
     */
    public void randomize(){
        Random r = new Random();
        this.beta = Math.abs(r.nextGaussian());
        this.slope = Math.abs(r.nextGaussian());
        this.slopePrim = Math.abs(r.nextGaussian());
        this.bias = Math.abs(r.nextGaussian());
        this.rbfNum = r.nextInt(20) + 1;
    }

    public Double getBeta() {
        return beta;
    }

    public void setBeta(Double beta) {
        this.beta = beta;
    }

    public Double getSlope() {
        return slope;
    }

    public void setSlope(Double slope) {
        this.slope = slope;
    }

    public Double getSlopePrim() {
        return slopePrim;
    }

    public void setSlopePrim(Double slopePrim) {
        this.slopePrim = slopePrim;
    }

    public Double getBias() {
        return bias;
    }

    public void setBias(Double bias) {
        this.bias = bias;
    }

    public Integer getRbfNum() {
        return rbfNum;
    }

    public void setRbfNum(Integer rbfNum) {
        this.rbfNum = rbfNum;
    }

    public TransferFunctionType getTransfer() {
        return transfer;
    }

    public void setTransfer(TransferFunctionType transfer) {
        this.transfer = transfer;
    }

    public ActivationFunctionType getActivation() {
        return activation;
    }

    public void setActivation(ActivationFunctionType activation) {
        this.activation = activation;
    }
}
