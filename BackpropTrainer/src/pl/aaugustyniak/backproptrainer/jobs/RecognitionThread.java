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
package pl.aaugustyniak.backproptrainer.jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.SerializationUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import pl.aaugustyniak.backproptrainer.model.BenchmarkedBp;
import pl.aaugustyniak.backproptrainer.model.TrainingEfficiencyOutcomeObserver;
import pl.aaugustyniak.backproptrainer.config.HibernateUtil;
import pl.aaugustyniak.backproptrainer.model.TestCase;
import pl.aaugustyniak.backproptrainer.cli.TrainingProcessReportFormatter;
import pl.aaugustyniak.backproptrainer.exceptions.BenchmarkedBpAnnException;
import pl.aaugustyniak.backproptrainer.model.entities.PeceptronTest;
import pl.aaugustyniak.backproptrainer.model.entities.TrainingMseReadout;
import pl.aaugustyniak.backproptrainer.model.entities.ValidationMseReadout;

/**
 * Thread job processing full train-test procedure for given case
 *
 * @author Artur Augustyniak <artur@aaugustyniak.pl>
 */
public class RecognitionThread extends Observable implements Runnable {

    protected TestCase tc;
    protected boolean saving = false;
    private Date dateStarted;

    /**
     * Constructor with valid Test Case object
     *
     * @param tc
     */
    public RecognitionThread(TestCase tc) {
        this.dateStarted = new Date();
        this.tc = tc;
    }

    /**
     * Start procedure
     */
    @Override
    public void run() {
        try {
            TrainingEfficiencyOutcomeObserver o = this.proceedTest();
            if (this.saving) {
                this.saveResults(o);
            } else {
                System.out.println(TrainingProcessReportFormatter.makeTrainingStats(o));
            }
            setChanged();
            notifyObservers();
            clearChanged();
        } catch (BenchmarkedBpAnnException ex) {
            Logger.getLogger(RecognitionThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Persist result
     *
     * @param o
     */
    private void saveResults(TrainingEfficiencyOutcomeObserver o) {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        synchronized (session) {
            session.beginTransaction();
            PeceptronTest e = new PeceptronTest();
            
            e.setDatasetLabel(o.getObservable().getTestCase().getName());
            e.setPerceptronLabel(o.getObservable().getNeuralNetwork().getLabel());
            e.setBpEpochs(o.getObservable().getCurrentIteration());
            e.setGoalTrainingMse(o.getObservable().getTestCase().getGoalMse());
            e.setTrainingMseResult(o.getTrnError().get(o.getTrnError().size() - 1));
            e.setValidationMseResult(o.getValiError().get(o.getValiError().size() - 1));
            e.setConfusionMatrix(SerializationUtils.serialize(o.getCofusionMatrix()));
            e.setEfficiencyFactorResult(o.getEfficiencyFactor().get(o.getEfficiencyFactor().size() - 1));
            e.setTrf(o.getTestClassificationAccuracy());
            e.setTotalPerceptronRate(o.getTestRatingMetric());
            e.setDateStarted(this.dateStarted);
            e.setDateFinished(new Date());

            Set<ValidationMseReadout> valiMseReadouts = new HashSet<ValidationMseReadout>(o.getValiError().size());
            for (int i = 0; i < o.getValiError().size(); i++) {
                ValidationMseReadout vMse = new ValidationMseReadout();
                vMse.setPeceptronTest(e);
                vMse.setReadoutNumber(i);
                vMse.setValue(o.getValiError().get(i));
                valiMseReadouts.add(vMse);
            }

            Set<TrainingMseReadout> trnMseReadouts = new HashSet<TrainingMseReadout>(o.getTrnError().size());
            for (int i = 0; i < o.getTrnError().size(); i++) {
                TrainingMseReadout tMse = new TrainingMseReadout();
                tMse.setPeceptronTest(e);
                tMse.setReadoutNumber(i);
                tMse.setValue(o.getTrnError().get(i));
                trnMseReadouts.add(tMse);
            }
            e.setTrainingMseReadouts(trnMseReadouts);
            e.setValidationMseReadouts(valiMseReadouts);
            session.save(e);
            session.getTransaction().commit();
            session.close();
            System.out.println(o.getObservable().getTestCase().getName() + o.getObservable().getNeuralNetwork().getLabel());
        }
    }

    /**
     * Benchmarked BP training on given TestCase object (Only on {L} learning
     * subset)
     *
     * @return
     */
    protected TrainingEfficiencyOutcomeObserver proceedTest() throws BenchmarkedBpAnnException {
        BenchmarkedBp bp = new BenchmarkedBp(tc);
        bp.setMaxIterations(this.tc.getEpochs());
        TrainingEfficiencyOutcomeObserver o = new TrainingEfficiencyOutcomeObserver(bp);
        bp.addObserver(o);
        bp.setMaxError(this.tc.getGoalMse());
        bp.bmLearn();
        return o;
    }

    /**
     * Check if database saving mode is enabled
     *
     * @return
     */
    public boolean isSaving() {
        return saving;
    }

    /**
     * Set database seving mode
     *
     * @param saving
     */
    public void setSaving(boolean saving) {
        this.saving = saving;
    }
}
