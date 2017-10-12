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
import org.neuroph.core.input.Intensity;
import org.neuroph.core.input.Max;
import org.neuroph.core.input.Min;
import org.neuroph.core.input.Product;
import org.neuroph.core.input.SumSqr;
import org.neuroph.core.input.SummingFunction;
import org.neuroph.core.input.WeightedInput;
import org.neuroph.core.input.WeightsFunction;
import org.neuroph.nnet.MultiLayerPerceptron;
import pl.aaugustyniak.backproptrainer.model.NetworkParams;

/**
 * Network using diffrent combinations of metrics and transfer functions
 *
 * @author Artur Augustyniak <artur@aaugustyniak.pl>
 */
public class VarryActivationPerceptron extends MultiLayerPerceptron {

    public static class MinkovskyInputFunction extends InputFunction {

        public class MinkovskyWeights extends WeightsFunction {

            @Override
            public double[] getOutput(List<Connection> inputConnections) {
                double[] outputVector = new double[inputConnections.size()];
                int i = 0;
                for (Connection connection : inputConnections) {
                    outputVector[i] = Math.abs(connection.getInput() - connection.getWeight().value);
                    i++;
                }
                return outputVector;
            }
        }

        public class MinkovskySumming extends SummingFunction {

            private Double m;

            public MinkovskySumming(Double m) {
                this.m = m;
            }

            @Override
            public double getOutput(double[] inputVector) {
                double sum = 0d;
                for (double input : inputVector) {
                    sum += Math.pow(input, this.m);
                }
                return Math.pow(sum, 1.0 / this.m);
            }
        }
        private WeightsFunction weightsFunction;
        private SummingFunction summingFunction;

        public MinkovskyInputFunction(Double m) {
            this.weightsFunction = new MinkovskyWeights();
            this.summingFunction = new MinkovskySumming(m);
        }

        @Override
        public double getOutput(List<Connection> inputConnections) {
            double[] inputVector = this.weightsFunction.getOutput(inputConnections);
            double output = this.summingFunction.getOutput(inputVector);
            return output;
        }
    }

    public enum ActivationFunctionType {

        WEIGHT_SUM_SQ,
        WEIGHT_PRODUCT,
        MIN,
        MAX,
        INTENSITY,
        MINKOVSKY_1,
        MINKOVSKY_2,
        MINKOVSKY_INF
    }

    public VarryActivationPerceptron(List<Integer> inLayers, NetworkParams p) {
        super(inLayers, p.getTransfer());
        for (int j = 1; j < this.getLayersCount() - 1; j++) {
            Layer lay = this.getLayerAt(j);
            for (Neuron ne : lay.getNeurons()) {
                switch (p.getActivation()) {
                    case WEIGHT_SUM_SQ:
                        ne.setInputFunction(new InputFunction(new WeightedInput(), new SumSqr()));
                    case WEIGHT_PRODUCT:
                        ne.setInputFunction(new InputFunction(new WeightedInput(), new Product()));
                    case MIN:
                        ne.setInputFunction(new InputFunction(new WeightedInput(), new Min()));
                    case MAX:
                        ne.setInputFunction(new InputFunction(new WeightedInput(), new Max()));
                    case INTENSITY:
                        ne.setInputFunction(new InputFunction(new WeightedInput(), new Intensity()));
                    case MINKOVSKY_1:
                        ne.setInputFunction(new MinkovskyInputFunction(1.0));
                    case MINKOVSKY_2:
                        ne.setInputFunction(new MinkovskyInputFunction(2.0));
                    case MINKOVSKY_INF:
                        ne.setInputFunction(new MinkovskyInputFunction(Double.MAX_VALUE));
                }
            }
        }
        this.setLabel(p.getTransfer() + " MLP activation: " + p.getActivation());
    }
}
