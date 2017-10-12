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

import org.neuroph.core.learning.TrainingSet;
import pl.aaugustyniak.backproptrainer.model.TestCase.LvtTrainingSet;

/**
 * Class implementing thumb-rule 80/20 like division of data set. whole set
 *
 * @author Artur Augustyniak <artur@aaugustyniak.pl>
 */
public class ParetoTrainingSet implements LvtTrainingSet {

    private TrainingSet completeSet;
    private TrainingSet trainingSet;
    private TrainingSet validationSet;
    private TrainingSet testSet;
    private DatasetTransformer shuffleTranform;

    /**
     * Create division of user dataset. Transform with derrived implementation
     *
     * @param completeSet
     * @param sf
     */
    public ParetoTrainingSet(TrainingSet completeSet, DatasetTransformer sf) {
        this.completeSet = completeSet;
        this.shuffleTranform = sf;
        this.shuffleTranform.transform(this.completeSet);
        this.completeSet.setLabel("Complete dataset.");
        TrainingSet[] tSubs;
        tSubs = this.completeSet.createTrainingAndTestSubsets(80, 20);
        this.testSet = tSubs[1];
        this.testSet.setLabel("Testing subset");
        tSubs = tSubs[0].createTrainingAndTestSubsets(80, 20);
        this.trainingSet = tSubs[0];
        this.trainingSet.setLabel("Training subset");
        this.validationSet = tSubs[1];
        this.validationSet.setLabel("Validation Subset");
    }

    @Override
    public TrainingSet getCompleteSet() {
        return completeSet;
    }

    @Override
    public TrainingSet getTrainingSet() {
        return trainingSet;
    }

    @Override
    public TrainingSet getValidationSet() {
        return validationSet;
    }

    @Override
    public TrainingSet getTestSet() {
        return testSet;
    }

    @Override
    public void shuffleAllSubsets() {
        this.completeSet.shuffle();
    }
}
