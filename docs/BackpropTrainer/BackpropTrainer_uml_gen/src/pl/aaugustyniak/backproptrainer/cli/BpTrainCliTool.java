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

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.cli.ParseException;
import pl.aaugustyniak.backproptrainer.config.AppPreferences;
import pl.aaugustyniak.backproptrainer.exceptions.DefinitionFileException;
import pl.aaugustyniak.backproptrainer.factories.CaseFactory;
import pl.aaugustyniak.backproptrainer.jobs.RecognitionThread;
import pl.aaugustyniak.backproptrainer.model.TestCase;

/**
 *
 * @author Artur Augustyniak <artur@aaugustyniak.pl>
 */
public class BpTrainCliTool {

    public static void doAction(String[] optionValues) throws ParseException {
        try {
            Integer runs = Integer.parseInt(optionValues[0]);
            boolean saving = (optionValues[1] != null && optionValues[1].equals("saving")) ? true : false;
            runProcess(runs, saving);

        } catch (Exception ex) {
            throw new ParseException(optionValues[0]);
        }
    }

    /**
     * Process tests given number of times, using given number of threads if
     * savingMode is set to true, outcomes will be persisted in db
     *
     * @param runs
     * @param threads
     * @param savingMode
     * @throws DefinitionFileException
     */
    private static void runProcess(Integer runs, boolean savingMode) throws DefinitionFileException {
        ExecutorService ex = Executors.newFixedThreadPool(Integer.parseInt(AppPreferences.getAppThreadsNum()));
        List<TestCase> tcases = CaseFactory.makeTestCases();
        for (int i = 0; i < runs; i++) {
            for (TestCase tcase : tcases) {
                RecognitionThread bt = new RecognitionThread(tcase);
                bt.setSaving(savingMode);
                ex.execute(bt);
                
            }
        }
        ex.shutdown();
    }
}
