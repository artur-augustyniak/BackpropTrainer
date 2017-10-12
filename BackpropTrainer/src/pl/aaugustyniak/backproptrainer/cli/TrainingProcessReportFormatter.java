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
package pl.aaugustyniak.backproptrainer.cli;

import com.google.common.base.Strings;
import pl.aaugustyniak.backproptrainer.model.TrainingEfficiencyOutcomeObserver;

/**
 * Helper class for statisitcs formatting
 *
 * @author Artur Augustyniak <artur@aaugustyniak.pl>
 */
public class TrainingProcessReportFormatter {

    /**
     * Prepare text stats and confiusion matrix
     *
     * @param o
     * @param caseLabel
     * @return
     */
    public static String makeTrainingStats(TrainingEfficiencyOutcomeObserver o) {
        synchronized (o) {
            String summary = "";
            if (o.getObservable().isStopped()) {
                summary += "Case label: " + o.getObservable().getTestCase().getName() + "\n";
                summary += "Perceptron label: " + o.getObservable().getNeuralNetwork().getLabel() + "\n";
                summary += "Goal was to stop with: " + o.getObservable().getMaxError() + " training MSE error\n";
                summary += "in: " + o.getObservable().getTestCase().getEpochs() + " max iterations\n";
                summary += "Goal reached or forced to stop at: \n";
                summary += o.getEpoch().get(o.getEpoch().size() - 1).intValue() + " iteration (Epoch)\n";
                summary += "Training MSE result: " + o.getTrnError().get(o.getTrnError().size() - 1) + "\n";
                summary += "Validation MSE result: " + o.getValiError().get(o.getValiError().size() - 1) + "\n";
                summary += "Efficiency factor result: " + o.getEfficiencyFactor().get(o.getEfficiencyFactor().size() - 1) + "\n";

                summary += "**\n";
                summary += "Total perceptron rate: " + o.getTestRatingMetric() + "\n";
                summary += "**\n";
                summary += "TRF in real test (with acceptance ramp):\n";
                summary += o.getTestClassificationAccuracy() + "\n";
                summary += "Cofiusion Matrix (without acceptance ramp)\n";
                int matEdge = o.getObservable().getTestCase().getOutputs();
                int i, j;
                summary += Strings.repeat("   -   ", matEdge) + "\n";
                for (i = 0; i < matEdge; i++) {
                    for (j = 0; j < matEdge; j++) {
                        summary += " | " + o.getCofusionMatrix()[i][j] + " | ";
                    }
                    summary += "\n";
                    summary += Strings.repeat("   -   ", matEdge) + "\n";
                }

            } else {
                summary = "Training in progress...\n";
            }

            return summary;
        }
    }
}
