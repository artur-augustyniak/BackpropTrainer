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
import org.neuroph.core.transfer.Sigmoid;
import org.neuroph.core.transfer.TransferFunction;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;
import pl.aaugustyniak.backproptrainer.model.NetworkParams;

/**
 * Network using bicentral localized neurons
 *
 * @author Artur Augustyniak <artur@aaugustyniak.pl>
 */
public class BicentralPerceptron extends MultiLayerPerceptron {

    public static class BicentralInputFunction extends InputFunction {

        public class BicentralWeights extends WeightsFunction {

            private Double slope;
            private Double slopePrim;
            private Double bias;
            private TransferFunction sigm = new Sigmoid();

            public BicentralWeights(Double slope, Double slopePrim, Double b) {
                this.slope = slope;
                this.slopePrim = slopePrim;
                this.bias = b;
            }

            @Override
            public double[] getOutput(List<Connection> inputConnections) {
                double[] outputVector = new double[inputConnections.size()];
                Double ti;
                Double stSigm;
                Double ndSigm;
                for (int i = 0; i < inputConnections.size() - 1; i++) {
                    ti = inputConnections.get(i).getWeight().getValue();
                    stSigm = Math.exp(this.slope) * (inputConnections.get(i).getInput() - ti + Math.exp(this.bias));
                    ndSigm = this.slopePrim * (inputConnections.get(i).getInput() - ti - Math.exp(this.bias));
                    outputVector[i] = this.sigm.getOutput(stSigm) * (1.0 - this.sigm.getOutput(ndSigm));
                }
                return outputVector;
            }
        }

        public class BicentralProduct extends SummingFunction {

            @Override
            public double getOutput(double[] inputVector) {
                double prod = 0d;
                for (double input : inputVector) {
                    prod *= input;
                }
                return prod;
            }
        }
        private WeightsFunction weightsFunction;
        private SummingFunction summingFunction;

        public BicentralInputFunction(Double s, Double sPrim, Double bias) {
            this.weightsFunction = new BicentralInputFunction.BicentralWeights(s, sPrim, bias);
            this.summingFunction = new BicentralInputFunction.BicentralProduct();
        }

        @Override
        public double getOutput(List<Connection> inputConnections) {
            double[] inputVector = this.weightsFunction.getOutput(inputConnections);
            double output = this.summingFunction.getOutput(inputVector);
            return output;
        }
    }

    public BicentralPerceptron(List<Integer> inLayers, NetworkParams p) {
        super(inLayers, TransferFunctionType.SIGMOID);

        for (int j = 1; j < this.getLayersCount() - 1; j++) {
            Layer lay = this.getLayerAt(j);
            for (Neuron ne : lay.getNeurons()) {
                ne.setInputFunction(new BicentralInputFunction(p.getSlope(), p.getSlopePrim(), p.getBias()));
            }
        }
        this.setLabel("Bicentral MLP slope:" + p.getSlope() + " slope':" + p.getSlopePrim() + " bias:" + p.getBias());
    }
}
