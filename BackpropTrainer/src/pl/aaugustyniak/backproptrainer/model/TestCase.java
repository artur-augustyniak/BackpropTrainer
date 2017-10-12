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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.SupervisedTrainingElement;
import org.neuroph.core.learning.TrainingElement;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.util.norm.MaxMinNormalizer;
import org.neuroph.util.norm.MaxNormalizer;
import org.neuroph.util.norm.Normalizer;
import pl.aaugustyniak.backproptrainer.arch.CbpPerceptron;
import pl.aaugustyniak.backproptrainer.exceptions.TcaseFileParseException;

/**
 * Test Case
 *
 * @author Artur Augustyniak <artur@aaugustyniak.pl>
 */
public class TestCase extends BufferedReader {

    public static final String DATA_DEL = "#DATA";
    public static final String COMMENT_DEL = "%";

    /**
     * Interface for TestCase. This is wrapper for TrainingSet objects divided
     * with LVT (learn-validation-test) scheme
     *
     * @author Artur Augustyniak <artur@aaugustyniak.pl>
     */
    public interface LvtTrainingSet {

        public interface DatasetTransformer {

            /**
             * Shuffles given TraininSet with derrived implementation
             *
             * @param t TrainingSet prepared for randomization
             */
            public void transform(TrainingSet t);
        }

        /**
         * Returns wrapped TrainingSet
         *
         * @return
         */
        public TrainingSet getCompleteSet();

        /**
         * Returns training subset
         *
         * @return
         */
        public TrainingSet getTrainingSet();

        /**
         * Returns validation subset
         *
         * @return
         */
        public TrainingSet getValidationSet();

        /**
         * Returns test subset
         *
         * @return
         */
        public TrainingSet getTestSet();

        /**
         * Force independent shuffle of all subsets
         */
        public void shuffleAllSubsets();
    }

    /**
     * Return return normalizer Object from .tcase string value
     */
    public enum NormalizerType {

        max(new MaxNormalizer()), maxmin(new MaxMinNormalizer());
        private Normalizer normalizer;

        private NormalizerType(Normalizer norm) {
            this.normalizer = norm;
        }

        public Normalizer getNormalizer() {
            return normalizer;
        }
    }

    /**
     * Run type Reference or Experimental
     */
    public enum RunType {

        REF,
        EXP
    }
    private String name;
    private String annLabel;
    private Integer epochs;
    private Double goalMse;
    private Normalizer nomalizer;
    private Integer inputs;
    private Integer outputs;
    private List<Integer> layersScheme;
    private TrainingSet rawTrainingSet;
    private LvtTrainingSet lvtTrainingSet;
    private NeuralNetwork ann;
    private RunType runType;

    /**
     * Create TestCase Object using reader with given file
     *
     * @param reader
     */
    public TestCase(Reader reader) throws TcaseFileParseException {
        super(reader);
        try {
            this.parseHeader();
            this.parseTrainingSet();
            if (this.rawTrainingSet.elements().isEmpty()) {
                throw new Exception("Empty training set.");
            }
            this.lvtTrainingSet = new ParetoTrainingSet(this.rawTrainingSet, new LvtTrainingSet.DatasetTransformer() {
                @Override
                public void transform(TrainingSet t) {
                    //t.shuffle();
                    nomalizer.normalize(t);
                }
            });
        } catch (Exception ex) {
            throw new TcaseFileParseException(ex.getMessage());
        }
    }

    /**
     * Set fresh (Randomized) perceptron
     *
     * @param ann
     */
    public void setAnn(NeuralNetwork ann) {
        this.ann = ann;
        this.ann.randomizeWeights(new Random());

    }

    public RunType getRunType() {
        return runType;
    }

    public void setRunType(RunType runType) {
        this.runType = runType;
    }

    /**
     * Get this test case network
     *
     * @return
     */
    public NeuralNetwork getNetwork() {
        return this.ann;
    }

    public String getAnnLabel() {
        return annLabel;
    }

    public void setAnnLabel(String annLabel) {
        this.annLabel = annLabel;
    }

    public String getName() {
        return name;
    }

    public Integer getEpochs() {
        return epochs;
    }

    public Double getGoalMse() {
        return goalMse;
    }

    public Normalizer getNomalizer() {
        return nomalizer;
    }

    public Integer getInputs() {
        return inputs;
    }

    public Integer getOutputs() {
        return outputs;
    }

    public List<Integer> getLayersScheme() {
        return layersScheme;
    }

    public LvtTrainingSet getLvtTrainingSet() {
        return lvtTrainingSet;
    }

    /**
     * Parse required header options
     *
     * @param line
     */
    private void parseParams(String line) {
        if (line.startsWith("name=")) {
            this.name = line.substring(5);
        }
        if (line.startsWith("epochs=")) {
            this.epochs = Integer.parseInt(line.substring(7));
        }
        if (line.startsWith("goal_mse=")) {
            this.goalMse = Double.parseDouble(line.substring(9));
        }
        if (line.startsWith("normalizer=")) {
            this.nomalizer = NormalizerType.valueOf(line.substring(11)).getNormalizer();
        }
        if (line.startsWith("in=")) {
            this.inputs = Integer.parseInt(line.substring(3));
        }
        if (line.startsWith("out=")) {
            this.outputs = Integer.parseInt(line.substring(4));
        }
        if (line.startsWith("ref_percept=")) {
            this.layersScheme = new ArrayList<Integer>();
            String[] strArray = line.substring(12).split(",");
            for (int i = 0; i < strArray.length; i++) {
                this.layersScheme.add(Integer.parseInt(strArray[i]));
            }
        }
        if (line.startsWith("percept_label=")) {
            this.annLabel = line.substring(14);
        }
    }

    /**
     * Parse .tcase file header values, strip comments
     *
     * @throws IOException
     */
    private void parseHeader() throws IOException {
        String thisLine;
        while ((thisLine = this.readLine()) != null && !thisLine.equals(DATA_DEL)) {
            if (thisLine.startsWith(COMMENT_DEL)) {
                continue;
            }
            this.parseParams(thisLine);
        }
    }

    /**
     * Parse csv .tcase file part and create NEUROPH Training set object. Taken
     * from package org.neuroph.core.learning;
     *
     * @author Zoran Sevarac <sevarac@gmail.com>
     * @throws IOException
     */
    private void parseTrainingSet() throws IOException {

        this.rawTrainingSet = new TrainingSet();
        this.rawTrainingSet.setInputSize(this.inputs);
        this.rawTrainingSet.setOutputSize(this.outputs);
        String line;
        while ((line = this.readLine()) != null) {
            double[] inp = new double[this.inputs];
            double[] out = new double[this.outputs];
            String[] values = line.split(",");
            if (values[0].equals("")) {
                continue; // skip if line was empty
            }
            for (int i = 0; i < this.inputs; i++) {
                inp[i] = Double.parseDouble(values[i]);
            }
            for (int i = 0; i < this.outputs; i++) {
                out[i] = Double.parseDouble(values[this.inputs + i]);
            }
            if (this.outputs > 0) {
                this.rawTrainingSet.addElement(new SupervisedTrainingElement(inp, out));
            } else {
                this.rawTrainingSet.addElement(new TrainingElement(inp));
            }
        }
    }
}
