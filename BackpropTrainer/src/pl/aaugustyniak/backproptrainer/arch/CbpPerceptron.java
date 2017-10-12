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
package pl.aaugustyniak.backproptrainer.arch;

import java.util.List;
import org.neuroph.core.Connection;
import org.neuroph.core.Layer;
import org.neuroph.core.Neuron;
import org.neuroph.core.input.InputFunction;
import org.neuroph.core.input.SummingFunction;
import org.neuroph.core.input.WeightsFunction;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;
import pl.aaugustyniak.backproptrainer.model.NetworkParams;

/**
 * Circular backpropagation network. Ridella, Sandro, Stefano Rovetta, and
 * Rodolfo Zunino. "Circular backpropagation networks for classification."
 * Neural Networks, IEEE Transactions on 8.1 (1997): 84-97.
 *
 * @author Artur Augustyniak <artur@aaugustyniak.pl>
 */
public class CbpPerceptron extends MultiLayerPerceptron {

    /**
     * Equations divided to fit into NEUROPH architecture
     */
    public static class RidellaInputFunction extends InputFunction {

        public class RidellaWeights extends WeightsFunction {

            @Override
            public double[] getOutput(List<Connection> inputConnections) {
                double[] outputVector = new double[inputConnections.size() + 1];
                Double squaredFactor = 0.0;

                for (int i = 0; i < inputConnections.size() - 1; i++) {
                    squaredFactor += Math.pow(inputConnections.get(i).getInput(), 2.0);
                    outputVector[i] = inputConnections.get(i).getWeightedInput();
                }
                outputVector[inputConnections.size() - 1] = squaredFactor * inputConnections.get(0).getWeight().getValue();
                outputVector[inputConnections.size()] = inputConnections.get(inputConnections.size() - 1).getWeight().getValue();
                return outputVector;
            }
        }

        /**
         * Equations dividet to fit into NEUROPH architecture
         */
        public class RidellaSumming extends SummingFunction {

            @Override
            public double getOutput(double[] inputVector) {
                double sum = 0d;
                for (double input : inputVector) {
                    sum += input;
                }
                return sum;
            }
        }
        private WeightsFunction weightsFunction;
        private SummingFunction summingFunction;

        public RidellaInputFunction() {
            this.weightsFunction = new RidellaWeights();
            this.summingFunction = new RidellaSumming();
        }

        @Override
        public double getOutput(List<Connection> inputConnections) {
            double[] inputVector = this.weightsFunction.getOutput(inputConnections);
            double output = this.summingFunction.getOutput(inputVector);
            return output;
        }
    }

    /**
     * Add input for square factor, may be provided in data set but we don't
     * want to modify sets or perceptron architecture. square factor wil be
     * calculated inside this class
     *
     * @param orig
     * @return
     */
    private static List<Integer> adjustHiddenLayersForCbp(List<Integer> orig) {
        for (int i = 1; i < orig.size() - 1; i++) {
            orig.set(i, orig.get(i) + 1);
        }
        return orig;
    }

    public CbpPerceptron(List<Integer> inLayers, NetworkParams params) {
        super(adjustHiddenLayersForCbp(inLayers), TransferFunctionType.SIGMOID);

        for (int j = 1; j < this.getLayersCount() - 1; j++) {
            Layer lay = this.getLayerAt(j);
            for (Neuron ne : lay.getNeurons()) {
                ne.setInputFunction(new RidellaInputFunction());
            }
        }
        this.setLabel("CBP");
    }
}
