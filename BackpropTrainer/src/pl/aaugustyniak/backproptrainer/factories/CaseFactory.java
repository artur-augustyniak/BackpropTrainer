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
package pl.aaugustyniak.backproptrainer.factories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;
import pl.aaugustyniak.backproptrainer.arch.VarryActivationPerceptron;
import pl.aaugustyniak.backproptrainer.config.AppPreferences;
import pl.aaugustyniak.backproptrainer.exceptions.DefinitionFileException;
import pl.aaugustyniak.backproptrainer.model.NetworkParams;
import pl.aaugustyniak.backproptrainer.model.TestCase;
import static pl.aaugustyniak.backproptrainer.model.TestCase.COMMENT_DEL;

/**
 * Provides all cases for experiment
 *
 * @author Artur Augustyniak <artur@aaugustyniak.pl>
 */
public class CaseFactory {

    public static final String PARAMS_DEL = "#PARAMS";

    /**
     * Creates test cases based on content of folders defined in
     * AppPreferences.tcase_folder and AppPreferences.mlp_folder
     *
     * @return
     */
    public static List<TestCase> makeTestCases() throws DefinitionFileException {

        List<TestCase> cs = new ArrayList<TestCase>();
        for (String file : getFilesPaths(AppPreferences.getTestCasesFolderPath(), "tcase")) {
            /**
             * Reference
             */
            String currentAnnDef = null;
            try {
                TestCase tc = new TestCase(new FileReader(file));
                NeuralNetwork n = new MultiLayerPerceptron(tc.getLayersScheme());
                n.setLabel(tc.getAnnLabel());
                tc.setRunType(TestCase.RunType.REF);
                tc.setAnn(n);
                cs.add(tc);
                System.out.println("New reference test case: " + tc.getName() + " - Network label: " + tc.getAnnLabel());
                /**
                 * Experimental
                 */
                for (String annDef : getFilesPaths(AppPreferences.getPerceptronsFolderPath(), "ann")) {
                    currentAnnDef = annDef;
                    TestCase expTc = new TestCase(new FileReader(file));
                    Class newAnn = Class.forName("pl.aaugustyniak.backproptrainer.arch." + determineType(annDef));
                    Constructor contruct = newAnn.getConstructor(List.class, NetworkParams.class);
                    NeuralNetwork expAnn = (NeuralNetwork) contruct.newInstance(tc.getLayersScheme(), getParamsObject(annDef));
                    expTc.setRunType(TestCase.RunType.EXP);
                    expTc.setAnnLabel(expAnn.getLabel());
                    expTc.setAnn(expAnn);
                    cs.add(expTc);
                    System.out.println("New experimental test case: " + expTc.getName() + " - Network label: " + expTc.getAnnLabel());
                }

            } catch (Exception ex) {
                throw new DefinitionFileException(currentAnnDef, file);
            }
        }
        return cs;
    }

    /**
     * List definition files
     *
     * @return
     */
    private static List<String> getFilesPaths(String folder, String ext) {
        List<String> files = new ArrayList<String>();
        File dir = new File(folder);
        try {
            for (File file : dir.listFiles()) {
                if (file.getName().endsWith(("." + ext))) {
                    files.add(file.getAbsolutePath());
                }
            }
            if (files.isEmpty()) {
                System.out.println("No ." + ext + " files in " + dir.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("Folder " + dir.getAbsolutePath() + " don't exist or You have no permissions to read it.");
        }

        return files;
    }

    /**
     * Parse ann definition file to get perceptron class name i.e.
     * class=MadalineNetwork
     *
     * @param fromFile
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static String determineType(String fromFile) throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fromFile));
        String thisLine;
        String className = null;
        while ((thisLine = reader.readLine()) != null && !thisLine.equals(PARAMS_DEL)) {
            if (thisLine.startsWith(COMMENT_DEL)) {
                continue;
            }
            if (thisLine.startsWith("class=")) {
                className = thisLine.substring(6);
            }
        }
        return className;
    }

    /**
     * Parse .ann file to build proper params object
     *
     * @param fromFile
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static NetworkParams getParamsObject(String fromFile) throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fromFile));
        String thisLine;
        NetworkParams params = new NetworkParams();
        boolean randomize = false;
        while ((thisLine = reader.readLine()) != null) {
            if (thisLine.startsWith(COMMENT_DEL)) {
                continue;
            }
            if (thisLine.startsWith("beta=")) {
                params.setBeta(Double.parseDouble(thisLine.substring(5)));
            }
            if (thisLine.startsWith("slope=")) {
                params.setSlope(Double.parseDouble(thisLine.substring(6)));
            }
            if (thisLine.startsWith("slope_prim=")) {
                params.setSlopePrim(Double.parseDouble(thisLine.substring(11)));
            }
            if (thisLine.startsWith("bias=")) {
                params.setBias(Double.parseDouble(thisLine.substring(5)));
            }
            if (thisLine.startsWith("rbf_num=")) {
                params.setRbfNum(Integer.parseInt(thisLine.substring(8)));
            }
            if (thisLine.startsWith("transfer=")) {
                params.setTransfer(TransferFunctionType.valueOf(thisLine.substring(9)));
            }
            if (thisLine.startsWith("activation=")) {
                params.setActivation(VarryActivationPerceptron.ActivationFunctionType.valueOf(thisLine.substring(11)));
            }
            if (thisLine.startsWith("RANDOM")) {
                randomize = true;
            }
        }
        if (randomize) {
            params.randomize();;
        }
        return params;
    }
}
