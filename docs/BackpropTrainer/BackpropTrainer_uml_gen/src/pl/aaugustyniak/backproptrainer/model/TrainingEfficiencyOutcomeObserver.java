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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import org.neuroph.core.learning.SupervisedTrainingElement;
import org.neuroph.core.learning.TrainingSet;

/**
 * Supervised learning process observer, observe process and make some real
 * testing after learning procees.
 *
 * @author Artur Augustyniak <artur@aaugustyniak.pl>
 */
public class TrainingEfficiencyOutcomeObserver implements Observer, Serializable {

    /*
     * Observable Trainer object
     */
    private BenchmarkedBp observable;
    /**
     * Series of grabbed learning epochs numbers Double for plots scalling
     */
    private List<Double> epoch;
    /**
     * Series of grabbed training total MSE mesurments after n-th epoch
     */
    private List<Double> trnError;
    /**
     * Series of grabbed validaion independent MSE mesurments after n-th epoch
     */
    private List<Double> valiError;
    /**
     * Series of computed efficiency values based on:
     *
     * MSE_validation / fabs(MSE_validation - MSE_train)
     *
     * This facor describes both learn and generalisation abilities
     */
    private List<Double> efficiencyFactor;
    /**
     * Cofiusion matrix for k-class clasiffiers
     */
    private int[][] cofusionMatrix;
    /**
     * Total Recog accuracy given by recognized over cutoff / tontal test
     * instances num
     */
    private Double testClassificationAccuracy;
    /**
     * In test iteration outupt with bigger vector mse will be rejected
     */
    private Double instanceMSEAcceptanceLevel;
    /**
     * Total tet raiting
     */
    private Double testRatingMetric;
    /**
     * Inner DSQR val for MSE computation
     */
    private Double vSquaredErrorSum;
    /**
     * num of properly recog in test part
     */
    private Integer trueRecognized;

    private TrainingEfficiencyOutcomeObserver() {
    }

    /**
     * Constructor with some observer pattern redudancy (due to NEUROPH
     * architecture)
     *
     * @param observable porcess to observe
     */
    public TrainingEfficiencyOutcomeObserver(BenchmarkedBp observable) {
        this.epoch = new ArrayList<Double>();
        this.trnError = new ArrayList<Double>();
        this.valiError = new ArrayList<Double>();
        this.efficiencyFactor = new ArrayList<Double>();
        this.observable = observable;
        Integer mSize = this.observable.getTestCase().getOutputs();
        this.cofusionMatrix = new int[mSize][mSize];
        this.instanceMSEAcceptanceLevel = 0.06;
    }

    /**
     * All Teaching proceses observers will be notified after each epoch (due to
     * NEUROPH architecture)
     *
     * @param o unused
     * @param o1 unused
     */
    @Override
    public void update(Observable o, Object o1) {
        /**
         * If observer was attached correctly
         */
        if (o == this.observable) {
            this.epoch.add(new Double(this.observable.getCurrentIteration()));
            Double lastTrainingMse = this.observable.getTotalNetworkError();
            this.trnError.add(lastTrainingMse);
            Double lastValidationMse = doValidationEpoch(this.observable.getTestCase().getLvtTrainingSet().getValidationSet());
            this.valiError.add(lastValidationMse);
            this.efficiencyFactor.add((1.0 / Math.exp(Math.abs(lastValidationMse - lastTrainingMse))) / Math.exp(lastValidationMse));
            if (this.observable.isStopped()) {
                this.testClassificationAccuracy = this.doTestEpoch(this.observable.getTestCase().getLvtTrainingSet().getTestSet());
                this.testRatingMetric = this.testClassificationAccuracy * this.efficiencyFactor.get(this.efficiencyFactor.size() - 1);
            }
        }
    }

    private Double doValidationEpoch(TrainingSet validationSet) {
        Double validationNetworkError = 0.0;
        this.vSquaredErrorSum = 0.0;
        Iterator<SupervisedTrainingElement> iterator = validationSet.iterator();
        while (iterator.hasNext()) {
            SupervisedTrainingElement supervisedTrainingElement = iterator.next();
            this.processPattern(supervisedTrainingElement);
        }
        /**
         * calculate total network error as MSE. Use MSE so network does not
         * grow with bigger training sets
         */
        validationNetworkError = this.vSquaredErrorSum / validationSet.size();
        return validationNetworkError;
    }

    private Double doTestEpoch(TrainingSet testSet) {
        Double recogAccuracy = 0.0;
        this.trueRecognized = 0;
        Iterator<SupervisedTrainingElement> iterator = testSet.iterator();
        while (iterator.hasNext()) {
            SupervisedTrainingElement trainingElement = iterator.next();
            this.observable.getNeuralNetwork().setInput(trainingElement.getInput());
            this.observable.getNeuralNetwork().calculate();
            double[] networkOutput = this.observable.getNeuralNetwork().getOutput();
            this.cofusionMatrix[this.largestElemIndex(networkOutput)][this.largestElemIndex(trainingElement.getDesiredOutput())] += 1;
            Double sampleMSE = this.calculateVSquaredErrorSum(
                    this.calculateVOutputError(networkOutput, trainingElement.getDesiredOutput()))
                    / trainingElement.getDesiredOutput().length;
            if (sampleMSE < this.instanceMSEAcceptanceLevel) {
                this.trueRecognized++;
            }
        }

        recogAccuracy = new Double(this.trueRecognized) / new Double(testSet.size());
        return recogAccuracy;
    }

    protected void processPattern(SupervisedTrainingElement trainingElement) {
        double[] input = trainingElement.getInput();
        this.observable.getNeuralNetwork().setInput(input);
        this.observable.getNeuralNetwork().calculate();
        double[] output = this.observable.getNeuralNetwork().getOutput();
        double[] desiredOutput = trainingElement.getDesiredOutput();
        double[] outputError = this.calculateVOutputError(desiredOutput, output);
        this.addToVSquaredErrorSum(outputError);
    }

    protected double[] calculateVOutputError(double[] desiredOutput, double[] output) {
        double[] outputError = new double[output.length];
        for (int i = 0; i < output.length; i++) {
            outputError[i] = desiredOutput[i] - output[i];
        }
        return outputError;
    }

    protected void addToVSquaredErrorSum(double[] outputError) {
        double outputErrorSqrSum = this.calculateVSquaredErrorSum(outputError);
        this.vSquaredErrorSum += outputErrorSqrSum;
    }

    protected Double calculateVSquaredErrorSum(double[] errorVector) {
        double outputErrorSqrSum = 0;
        for (double er : errorVector) {
            outputErrorSqrSum += (er * er) * 0.5;
        }
        return outputErrorSqrSum;
    }

    protected int largestElemIndex(double[] array) {
        double largest = array[0];
        int index = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > largest) {
                largest = array[i];
                index = i;
            }
        }
        return index;
    }

    public BenchmarkedBp getObservable() {
        return observable;
    }

    public List<Double> getEpoch() {
        return epoch;
    }

    public List<Double> getTrnError() {
        return trnError;
    }

    public List<Double> getValiError() {
        return valiError;
    }

    public List<Double> getEfficiencyFactor() {
        return efficiencyFactor;
    }

    public int[][] getCofusionMatrix() {
        return cofusionMatrix;
    }

    public Double getTestClassificationAccuracy() {
        return testClassificationAccuracy;
    }

    public Double getInstanceMSEAcceptanceLevel() {
        return instanceMSEAcceptanceLevel;
    }

    public void setInstanceMSEAcceptanceLevel(Double instanceMSEAcceptanceLevel) {
        this.instanceMSEAcceptanceLevel = instanceMSEAcceptanceLevel;
    }

    public Double getTestRatingMetric() {
        return testRatingMetric;
    }
}
