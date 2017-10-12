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
import org.neuroph.core.Layer;
import org.neuroph.core.Neuron;
import org.neuroph.core.transfer.Tanh;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;
import pl.aaugustyniak.backproptrainer.model.NetworkParams;

/**
 * Biipolar Sigmoidal Perceptron with adjustable transfer function steepness
 *
 * @author Artur Augustyniak <artur@aaugustyniak.pl>
 */
public class TanhPerceptron extends MultiLayerPerceptron {

    public TanhPerceptron(List<Integer> inLayers, NetworkParams p) {
        super(inLayers, TransferFunctionType.TANH);
        for (int j = 1; j < this.getLayersCount() - 1; j++) {
            Layer lay = this.getLayerAt(j);
            for (Neuron ne : lay.getNeurons()) {
                ne.setTransferFunction(new Tanh(p.getBeta()));
            }
        }
        this.setLabel("Tanh MLP - beta:" + p.getBeta());
    }
}
