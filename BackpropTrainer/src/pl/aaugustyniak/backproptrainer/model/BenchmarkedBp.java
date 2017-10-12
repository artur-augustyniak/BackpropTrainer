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

import org.neuroph.nnet.learning.BackPropagation;
import pl.aaugustyniak.backproptrainer.exceptions.BenchmarkedBpAnnException;

/**
 * Benchmarked ovverride of NEUROPH Backpropagation Trainer
 *
 * @author Artur Augustyniak <artur@aaugustyniak.pl>
 */
public class BenchmarkedBp extends BackPropagation {

    private TestCase tc;

    public BenchmarkedBp(TestCase tc) {
        super();
        this.tc = tc;
        this.setNeuralNetwork(tc.getNetwork());
    }

    /**
     * Benchmarked Learn. Side effect is T Efficiency Observer with process
     * statistics
     */
    public void bmLearn() throws BenchmarkedBpAnnException {
        try {
            this.learn(tc.getLvtTrainingSet().getTrainingSet());
        } catch (Exception e) {
            throw new BenchmarkedBpAnnException(e.getMessage());
        }
    }

    public TestCase getTestCase() {
        return tc;
    }

    @Override
    protected void beforeEpochStart() {
        super.beforeEpochStart();
        this.tc.getLvtTrainingSet().getTrainingSet().shuffle();
    }
}
